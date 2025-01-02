package org.example.pizzastore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/*
* TODO: apply discount.
* */
public class PizzaStore {

    public static void main(String[] args) {
        Pizza pizza = new Pizza(PizzaBase.THIN_CRUST, PizzaSize.S, new ArrayList<>(), 100);
        pizza.addTopping(new Topping(1, "cheese", 1));
        pizza.addTopping(new Topping(1, "mushroom", 2));
        System.out.println(pizza.getDescription());

        pizza.setBase(PizzaBase.THICK_CRUST);

        Drink drink = new Drink("coke",  DrinkSize.L, 10.0);

        Order order = new Order("1", new ArrayList<>());
        order.addItem(pizza);
        order.addItem(drink);

        order.addItem(new SideDish("ice cream", 5));

        System.out.println(order.getOrderDescription());
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

interface OrderableItem {
    double calculatePrice();
    String getDescription();
}

@AllArgsConstructor
@Data
class Pizza implements OrderableItem {
    PizzaBase base;
    PizzaSize size;
    List<Topping> toppings;
    double basePrice;

    @Override
    public double calculatePrice() {
        return (basePrice + toppings.stream().mapToDouble(Topping::getPrice).sum())
                * base.getMultiplier() * size.getMultiplier();
    }

    public void addTopping(Topping topping) {
        toppings.add(topping);
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: pizza \n");
        sb.append("Base: ").append(base).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Topping: ");
        if (toppings.isEmpty()) {
            sb.append("None.");
        } else {
            sb.append(String.join(", ", toppings.stream().map(Topping::getName).toList()));
        }
        sb.append("\n");
        sb.append("Total = ").append(calculatePrice()).append("\n");
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

@Getter
enum DrinkSize {
    S(1.0), M(1.1), L(1.2);

    private final double multiplier;

    DrinkSize(double multiplier) {
        this.multiplier = multiplier;
    }
}

@AllArgsConstructor
@Data
class Drink implements OrderableItem {
    String name;
    DrinkSize size;
    double basePrice;

    @Override
    public double calculatePrice() {
        return basePrice * size.getMultiplier();
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Size: ").append(size).append("\n");
        sb.append("Total = ").append(calculatePrice()).append("\n");
        return sb.toString();
    }
}

@AllArgsConstructor
class Order {

    String id;

    List<OrderableItem> items;

    public void addItem(OrderableItem item) {
        items.add(item);
    }

    public double calculateTotal() {
        return items.stream().mapToDouble(OrderableItem::calculatePrice).sum();
    }

    public String getOrderDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(id).append("\n\n").append("Items:\n");
        items.forEach(item -> sb.append(item.getDescription()).append("\n"));
        sb.append("Total Price: ").append(calculateTotal());
        return sb.toString();
    }
}

@AllArgsConstructor
@Data
class SideDish implements OrderableItem {
    String name;
    double price;

    @Override
    public double calculatePrice() {
        return price;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Total = ").append(calculatePrice()).append("\n");
        return sb.toString();
    }
}