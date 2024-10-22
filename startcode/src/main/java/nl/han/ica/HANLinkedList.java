package nl.han.ica;
import nl.han.ica.datastructures.IHANLinkedList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private ArrayList<Node> nodes;

    private class Node {
        T data;
        Node next;

        Node(T data) {
            this.data = data;
        }
    }

    public HANLinkedList() {
        nodes = new ArrayList<>();
    }

    @Override
    public void addFirst(T value) {
        Node newNode = new Node(value);
        if (!nodes.isEmpty()) {
            newNode.next = nodes.get(0);
        }
        nodes.add(0, newNode);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > nodes.size()) {
            throw new IndexOutOfBoundsException();
        }
        Node newNode = new Node(value);
        if (index < nodes.size()) {
            newNode.next = nodes.get(index);
        }
        nodes.add(index, newNode);
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= nodes.size()) {
            throw new IndexOutOfBoundsException();
        }
        nodes.remove(pos);
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= nodes.size()) {
            throw new IndexOutOfBoundsException();
        }
        return nodes.get(pos).data;
    }

    @Override
    public void removeFirst() {
        if (nodes.isEmpty()) {
            throw new NoSuchElementException("The list is empty.");

        }
        nodes.remove(0);
    }

    @Override
    public T getFirst() {
        if (nodes.isEmpty()) {
            throw new NoSuchElementException("The list is empty.");

        }
        return nodes.get(0).data;
    }

    @Override
    public int getSize() {
        return nodes.size();
    }

//    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < nodes.size();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return nodes.get(currentIndex++).data;
            }
        };
    }
}