package org.example.vendingmachine;


import lombok.Data;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;


@Data
public class VendingMachine {

    private final IdleState idleState;

    private final CoinInsertedState coinInsertedState;

    private final DispenseItemState dispenseItemState;

    private VendingMachineState currentState;

    private final Inventory inventory;

    private int money;

    public VendingMachine() {
        idleState = new IdleState(this);
        coinInsertedState = new CoinInsertedState(this);
        dispenseItemState = new DispenseItemState(this);
        currentState = idleState;
        inventory = new Inventory();
        money = 0;
    }

    public void addItem(Item item, int quantity) {
        inventory.addItem(item, quantity);
    }

    public void insertCoin(int coin) {
        currentState.insertCoin(coin);
    }

    public void selectItem(Item item) {
        currentState.selectItem(item);
    }

    public void dispenseItem(Item item) {
        currentState.dispenseItem(item);
    }

    public void addMoney(int value) {
        money += value;
    }

    public void refundMoney(Item item) {
        System.out.printf("refund money %d. %n", money - item.getPrice());
        money = 0;
    }

    public static void main(String[] args) {
        VendingMachine vendingMachine = new VendingMachine();

        Item item1 = new Item("apple", "apple", 1);
        Item item2 = new Item("banana", "banana", 2);
        Item item3 = new Item("pineapple", "pineapple", 3);
        vendingMachine.addItem(item1, 10);
        vendingMachine.addItem(item2, 10);
        vendingMachine.addItem(item3, 10);

        vendingMachine.selectItem(item1);

        vendingMachine.insertCoin(1);
        vendingMachine.selectItem(item1);
        vendingMachine.dispenseItem(item1);

        vendingMachine.insertCoin(1);
        vendingMachine.selectItem(item2);
        vendingMachine.dispenseItem(item2);

        vendingMachine.insertCoin(5);
        vendingMachine.selectItem(item2);
        vendingMachine.dispenseItem(item2);

        Item orange = new Item("orange", "orange", 1);
        vendingMachine.insertCoin(1);
        vendingMachine.selectItem(orange);
    }

}

@Value
class Item {
    String id;
    String name;
    int price;
}

class Inventory {
    Map<Item, Integer> itemStock;

    public Inventory() {
        itemStock = new HashMap<>();
    }

    public void addItem(Item item, int quantity) {
        itemStock.put(item, getStock(item) + quantity);
    }

    public boolean decreaseStock(Item item) {
        int stock = getStock(item);
        if (stock == 0) {
            System.out.printf("item %s is out of stock. %n", item.getName());
            return false;
        }
        itemStock.put(item, stock - 1);
        return true;
    }

    private int getStock(Item item) {
        return itemStock.getOrDefault(item, 0);
    }

}

interface VendingMachineState {
    void insertCoin(int value);
    void selectItem(Item item);
    void dispenseItem(Item item);
}

class IdleState implements VendingMachineState {

    VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int value) {
        machine.addMoney(value);
        machine.setCurrentState(machine.getCoinInsertedState());
    }

    @Override
    public void selectItem(Item item) {
        System.out.printf("please insert coins first. %n");
    }

    @Override
    public void dispenseItem(Item item) {
        System.out.printf("please insert coins first. %n");
    }
}

class CoinInsertedState implements VendingMachineState {

    VendingMachine machine;

    public CoinInsertedState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int value) {
        machine.addMoney(value);
    }

    @Override
    public void selectItem(Item item) {
        if (machine.getMoney() < item.getPrice()) {
            System.out.printf("Insufficient coin for %s, please insert more %d coin(s). %n",
                    item.getName(), item.getPrice() - machine.getMoney());
            return;
        }
        boolean success = machine.getInventory().decreaseStock(item);
        if (!success) {
            System.out.printf("item %s is out of stock. please choose other %n", item.getName());
            return;
        }
        System.out.printf("item %s is selected. %n", item.getName());
        machine.setCurrentState(machine.getDispenseItemState());
        machine.refundMoney(item);
    }

    @Override
    public void dispenseItem(Item item) {
        System.out.printf("please select a item first. %n");
    }
}

class DispenseItemState implements VendingMachineState {

    VendingMachine machine;

    public DispenseItemState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int value) {
        System.out.printf("vending machine is busy, please wait a second. %n");
    }

    @Override
    public void selectItem(Item item) {
        System.out.printf("vending machine is busy, please wait a second. %n");
    }

    @Override
    public void dispenseItem(Item item) {
        System.out.printf("item %s is dispensed. %n", item.getName());
        machine.setCurrentState(machine.getIdleState());
    }
}
