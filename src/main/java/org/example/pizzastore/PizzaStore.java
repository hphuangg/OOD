package org.example.pizzastore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PizzaStore {

    public static void main(String[] args) {
        Pizza pizza = new Pizza(PizzaBase.THIN_CRUST, PizzaSize.S, new ArrayList<>(), 100);
        pizza.addTopping(new Topping(1, "cheese", 1));
        pizza.addTopping(new Topping(1, "mushroom", 2));

        // (100+1+2) * 1* 1
        System.out.println(pizza.getDescription());


        // (100+1+2) * 1.2 * 1
        pizza.setBase(PizzaBase.THICK_CRUST);

        System.out.println(pizza.getDescription());
    }

}

@Getter
enum PizzaSize {
    S(1.0), M(1.2), L(1.5), XL(2.0);

    private final double multiplier;
    PizzaSize(double multiplier) {
        this.multiplier = multiplier;
    }

}

@Getter
enum PizzaBase {
    THIN_CRUST(1.0), THICK_CRUST(1.2), GLUTEN_FREE(1.5);

    private final double multiplier;
    PizzaBase(double multiplier) {
        this.multiplier = multiplier;
    }
}

@AllArgsConstructor
@Data
class Pizza {
    PizzaBase base;
    PizzaSize size;
    List<Topping> toppings;
    double basePrice;

    public double calculatePrice() {
        return (basePrice + toppings.stream().mapToDouble(Topping::getPrice).sum())
                * base.getMultiplier() * size.getMultiplier();
    }

    public void addTopping(Topping topping) {
        toppings.add(topping);
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Base: ").append(base).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Topping: ");
        if (toppings.isEmpty()) {
            sb.append("None.");
        } else {
            sb.append(String.join(", ", toppings.stream().map(Topping::getName).toList()));
        }
        sb.append("\n");
        sb.append("total = ").append(calculatePrice()).append("\n");
        return sb.toString();
    }
}

@AllArgsConstructor
@Data
class Topping {
    int id;
    String name;
    double price;
}
