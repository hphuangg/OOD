package org.example.lrucache;


import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
*  TODO: ReentrantLock, ReentrantReadWriteLock
* */

@Getter
class ListNode {
    int key;
    int value;
    ListNode prev;
    ListNode next;

    public ListNode (int key, int value) {
        this.key = key;
        this.value = value;
    }
}

class LRUCache {
    /**
     DoublyLinkedList stores insert values
     HashMap to store the input key and corresponding ListNode

     claas ListNode
     - value,
     - next: ListNode


     when put(key value)
     if (map.containsKey(key))
     put (key new ListNode(val)) hashMap

     key val
     1. ListNode(1)


     0  <-> (1).<-> null
     ^
     most recent used head (mruHead) - to insert new node
     mru.prev.prev.next = newNode
     newNode.prev = mru.prev
     mru.prev = newNode
     newNode.next = mru
     ^
     lruHead

     check the capcaity, oversize
     remove lruNode
     - lruHead.next = lruHead.next.next

     update key and new node to hasmap


     if contains key
     ListNode nocde = map.get(key)
     deleteNode(node)

     perform insert opertation


     1. delete existing node
     2. insert this node
     return node.getValue()

     */

    Map<Integer, ListNode> map;

    ListNode lruHead;

    ListNode mruHead;

    int capacity;

    /**
     (0,0) <---->  (0,0)
     ^lruHead.   ^ mruHead

     */

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();
        this.lruHead = new ListNode(0, 0);
        this.mruHead = new ListNode(0, 0);
        lruHead.next = mruHead;
        mruHead.prev = lruHead;
    }

    public int get(int key) {
        System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - get (start)");
        if (!map.containsKey(key)) return -1;
        ListNode node = map.get(key);

        synchronized (this) {
            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - get (synchronized start)");
            // update to mru
            removeNode(node);
            insertNode(node);
            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - get (synchronized end)");
        }


        System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - get (end)");
        return node.getValue();
    }

    public void put(int key, int value) {
        System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - put (start)");
        // if already exist in the list, delete the node.
        synchronized (this) {
            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - put (synchronized - start)");

            if (map.containsKey(key)) {
                ListNode existingNode = map.get(key);
                existingNode.value = value;
                removeNode(existingNode);
                insertNode(existingNode);
            } else {
                ListNode node = new ListNode(key, value);
                insertNode(node);
                map.put(key, node);
            }

            // evict lru node
            if (map.size() > capacity) {
                ListNode evictedNode = lruHead.next;
                removeNode(evictedNode);
                map.remove(evictedNode.getKey());
            }

            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - put (synchronized - end)");

        }

        System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - put (end)");
    }

    private void insertNode(ListNode node) {
        mruHead.prev.next = node;
        node.prev = mruHead.prev;
        node.next = mruHead;
        mruHead.prev = node;
    }

    private void removeNode(ListNode node) {
        ListNode prevNode = node.prev;
        ListNode nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(5);

        new Thread(() -> cache.put(1, 1), "thread 1").start();
        new Thread(() -> cache.put(2, 2), "thread 2").start();
        new Thread(() -> System.out.println(cache.get(1)), "thread 3").start();
        new Thread(() -> cache.put(3, 3), "thread 4").start(); // Evicts key 2

    }

}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */