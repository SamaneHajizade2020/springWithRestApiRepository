package kitchen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
public class IngredientController {
    private final static Logger log = Logger.getLogger(IngredientController.class.getName());

    @Autowired
    IngredientRepository repository;

    public IngredientController(IngredientRepository repository){this.repository = repository;};

    @RequestMapping(value = "/inventory", method = RequestMethod.GET)
    public ResponseEntity<Object> getInventory() {
        List<Ingredient> repositoryAll = repository.findAll();
        for (Ingredient ingredient : repositoryAll) {
            if(ingredient.getQuantity() == 0)
                repository.delete(ingredient);
        }
        return new ResponseEntity<>(repositoryAll, HttpStatus.OK);
    }

    @RequestMapping(value = "/createIngredient", method = RequestMethod.POST)
    public ResponseEntity<Object> createIngredientWithoutLimit(@RequestBody ArrayList<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            if(ingredient.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(ingredient);
        }
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/{id}", method = RequestMethod.PUT)
    public Ingredient replaceIngredient(@RequestBody Ingredient newIngredient, @PathVariable Long id) {
        return repository.findById(id)
                .map(ingredient -> {
                    ingredient.setName(newIngredient.getName());
                    ingredient.setQuantity(newIngredient.getQuantity());
                    return repository.save(ingredient);
                })
                .orElseGet(() -> {
                    newIngredient.setId(id);
                    return repository.save(newIngredient);
                });
       // return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/fill", method = RequestMethod.POST)
    public ResponseEntity<Object> createIngredient(@RequestBody ArrayList<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            if(ingredient.getQuantity() <= 0) {
                return  new ResponseEntity<>("Rejected cause of zero or negative quantity", HttpStatus.NOT_ACCEPTABLE);
            }
            repository.save(ingredient);
            log.info("Ingredient that is added to ingredients list: "  + ingredient.getId() + ingredient.getName() + ingredient.getQuantity());
        }

        controlIngredientQuantity();
        return new ResponseEntity<>("Product is created successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "/inventory/deleteAll", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteAll() {
        List<Ingredient> all = repository.findAll();
        for (Ingredient ingredient : all) {
            repository.delete(ingredient);
        }
        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/inventory/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        Optional<Ingredient> ingredientId = repository.findById(Long.valueOf(id));
        if(ingredientId.isPresent())
        repository.deleteById(id);
        return new ResponseEntity<>("Product is deleted successsfully", HttpStatus.OK);
    }

    public void controlIngredientQuantity() {
        List<Ingredient> newListOFIngredients = new ArrayList<>();
        List<Ingredient> removableListOFDuplicateIngredient = new ArrayList<>();
        List<Ingredient> ingredients = repository.findAll();

        List<Ingredient> listDuplicateIngredient = listDuplicateIngredient(ingredients);
        log.info("Size of Duplicate list:" + listDuplicateIngredient.size());

        if (listDuplicateIngredient.size() > 0) {
            for (Ingredient ingredient : listDuplicateIngredient) {
                log.info("ingredient in duplicate List: " + ingredient.getId() + ingredient.getName() + ingredient.getQuantity());

                Ingredient newIngredient = new Ingredient(ingredient.getName(), sumOfSameQuantity(listDuplicateIngredient, ingredient));
                log.info("newIngredient: " + newIngredient.getId() + newIngredient.getName() + newIngredient.getQuantity());

                Ingredient newIngredientInTable = repository.save(new Ingredient(newIngredient.getName(), newIngredient.getQuantity()));
                log.info("newIngredientInTable: " + newIngredientInTable.getId() + newIngredientInTable.getName() + newIngredientInTable.getQuantity());

                newListOFIngredients.add(newIngredientInTable);
                for (Ingredient gred : newListOFIngredients) {
                    log.info("gred in newListOFIngredients: " + gred.getId() + gred.getName() + gred.getQuantity());
                }
                log.info("sizeOfNewListOFIngredient: " + newListOFIngredients.size()
                        + " " + "Remove duplicate from new list: " + removeDuplicateIngredient(newListOFIngredients));
                removableListOFDuplicateIngredient.add(ingredient);
            }

            for (Ingredient ingredient : listDuplicateIngredient) {
                log.info("ingredient that will be removed: " + ingredient.getId() + ingredient.getName() + ingredient.getQuantity());
                repository.deleteById(ingredient.getId());
                repository.delete(ingredient);
            }
        }
    }

    public List<Ingredient> listDuplicateIngredient(Collection<Ingredient> ingredients) {
        return ingredients.stream()
                .collect(Collectors.groupingBy(Ingredient:: getName))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .collect(Collectors.toList());
    }

    public Integer sumOfSameQuantity(List<Ingredient> ingredients, Ingredient ingredient){
        return ingredients.stream()
                .filter(customer -> ingredient.getName().equals(customer.getName())).map(x -> x.getQuantity()).reduce(0, Integer::sum);

    }

    public boolean removeDuplicateIngredient(List<Ingredient> listIngredient) {
        boolean flag=false;

        for (int i = 0; i < listIngredient.size(); i++) {
            for (int j = 0; j < i; j++) {
                if ((listIngredient.get(i).getName().equalsIgnoreCase(listIngredient.get(j).getName())) &&
                        (listIngredient.get(i).getQuantity().compareTo(listIngredient.get(j).getQuantity())==0)
                        && (i!=j)){
                    Ingredient ingredient = listIngredient.get(i);
                    log.info("It want to be removed: " + ingredient.getId() + ingredient.getName() + ingredient.getQuantity());

                    if(ingredient.getId() != null){
                        log.info("Ingredient Id to be remover:" + ingredient.getId());
                        listIngredient.remove(ingredient);
                        repository.delete(ingredient);
                        flag= true;
                    }
                    else
                        flag= false;
                }
            }
        }
        return flag;
    }
}

