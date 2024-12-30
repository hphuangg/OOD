package org.example.coffeevendingmachine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class CoffeeVendingMachine {

    Map<String, Drink> drinks;

    Inventory inventory;

//    public void initialDefaultDrink() {
//        drinks.put("Espresso", new Espresso(new Recipe(Map.of(Ingredient.COFFEE_BEEN, 3, Ingredient.WATER, 1))));
//        drinks.put("Americano", new Americano(new Recipe(Map.of(Ingredient.COFFEE_BEEN, 2, Ingredient.WATER, 3))));
//        drinks.put("Latte", new Latte(new Recipe(Map.of(Ingredient.COFFEE_BEEN, 3, Ingredient.WATER, 2, Ingredient.MILK, 2))));
//    }

    public void initialDefaultDrink() {
        drinks.put("Espresso", new Drink("Espresso", new Recipe(Map.of(Ingredient.COFFEE_BEEN, 3, Ingredient.WATER, 1))));
        drinks.put("Americano", new Drink("Americano", new Recipe(Map.of(Ingredient.COFFEE_BEEN, 2, Ingredient.WATER, 3))));
        drinks.put("Latte", new Drink("Latte", new Recipe(Map.of(Ingredient.COFFEE_BEEN, 3, Ingredient.WATER, 2, Ingredient.MILK, 2))));
    }

    public void refillIngredient(Ingredient ingredient, int count) {
        inventory.increment(ingredient, count);
    }

    public void displayStock() {
        inventory.displayStock();
    }

    public void displayMenu() {
        // check stock and mark out of stock items.
        for (String drinkName : drinks.keySet()) {
            Drink drink = drinks.get(drinkName);
            System.out.println(drinkName + (!canMake(drink) ? " (unavailable)" : ""));
        }
    }

    private boolean canMake(Drink drink) {
        for (Map.Entry<Ingredient, Integer> entry : drink.getRecipe().getIngredients().entrySet()) {
            if (!inventory.validateStock(entry.getKey(), entry.getValue())) {
                System.out.printf("%s is unavailable, %s is out of stock. \n", drink.getName(), entry.getKey());
                return false;
            }
        }
        return true;
    }

    public Drink selectCoffee(String name) {
        return drinks.get(name);
    }

    public Drink customize(String name, Recipe recipe) {
        Drink drink = new Drink(name + " (Customized)", recipe);
        if (!canMake(drink)) {
            System.out.printf("%s is unavailable. \n", drink.getName());
            return null;
        }
        return drink;
    }

    public Drink make(Drink drink) {
        if (drink == null) return null;
        for (Map.Entry<Ingredient, Integer> entry : drink.getRecipe().getIngredients().entrySet()) {
            inventory.decrement(entry.getKey(), entry.getValue());
        }

        // check stock and notify admin asynchronously
        return drink;
    }

    public static void main(String[] args) {
        CoffeeVendingMachine machine = new CoffeeVendingMachine(new HashMap<>(), new Inventory());
        machine.initialDefaultDrink();

        for (Ingredient ingredient : Ingredient.values()) {
            machine.refillIngredient(ingredient, 10);
        }

        machine.displayStock();

        machine.displayMenu();

        Drink drink = machine.selectCoffee("Espresso");
        drink.getRecipe().display();

        Drink customized = machine.customize("Espresso", new Recipe(Map.of(Ingredient.COFFEE_BEEN, 10, Ingredient.WATER, 1)));
        customized.getRecipe().display();

        machine.make(customized);
        machine.displayStock();
        machine.displayMenu();

        machine.refillIngredient(Ingredient.COFFEE_BEEN, 3);
        machine.displayStock();
        machine.displayMenu();

        // try another customize
        Drink customized2 = machine.customize("Espresso", new Recipe(Map.of(Ingredient.COFFEE_BEEN, 10, Ingredient.WATER, 1)));
    }
}

enum Ingredient {
    COFFEE_BEEN, MILK, WATER
}

@AllArgsConstructor
@Data
class Recipe {

    Map<Ingredient, Integer> ingredients;

    public void display() {
        System.out.println("ingredient, count");
        for (Ingredient ingredient : ingredients.keySet()) {
            System.out.printf("%s, %d \n",
                    ingredient.toString(), ingredients.getOrDefault(ingredient, 0));
        }
    }
}

@Data
@AllArgsConstructor
class Drink {

    String name;

    Recipe recipe;

//    public void customize(Recipe cusomtizedRecipe) {
//        if (cusomtizedRecipe == null) {
//            return;
//        }
//
//        this.recipe = cusomtizedRecipe;
//    }
}

//@AllArgsConstructor
//@Data
//abstract class Drink {
//
//    Recipe recipe;
//
//    public void customize(Recipe cusomtizedRecipe) {
//        if (cusomtizedRecipe == null) {
//            return;
//        }
//
//        this.recipe = cusomtizedRecipe;
//    }
//}
//
//class Espresso extends Drink {
//
//    public Espresso(Recipe recipe) {
//        super(recipe);
//    }
//}
//
//class Americano extends Drink {
//
//    public Americano(Recipe recipe) {
//        super(recipe);
//    }
//}
//
//class Latte extends Drink {
//
//    public Latte(Recipe recipe) {
//        super(recipe);
//    }
//}

@NoArgsConstructor
class Inventory {
    Map<Ingredient, Integer> ingredientCount;

    // refill
    public void increment(Ingredient ingredient, int count) {
        if (ingredientCount == null) {
            ingredientCount = new HashMap<>();
        }
        ingredientCount.put(ingredient, getStock(ingredient) + count);
    }

    // cost
    public boolean decrement(Ingredient ingredient, int cost) {
        int stock = getStock(ingredient);
        if (stock < cost) {

            return false;
        }
        if (!validateStock(ingredient, cost)) {
            System.out.printf("ingredient: %s is out of stock.", ingredient.toString());
            return false;
        }
        increment(ingredient, -cost);
        return true;
    }

    public boolean validateStock(Ingredient ingredient, int cost) {
        int stock = getStock(ingredient);
        return stock >= cost;
    }

    private int getStock(Ingredient ingredient) {
        return ingredientCount.getOrDefault(ingredient, 0);
    }

    public void displayStock() {
        System.out.printf("ingredient, stock\n");
        for (Ingredient ingredient : ingredientCount.keySet()) {
            System.out.printf("%s, %d \n", ingredient.toString(), getStock(ingredient));
        }
    }
}
