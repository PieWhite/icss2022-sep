package nl.han.ica;

import nl.han.ica.datastructures.IHANStack;

public class HANStack<T> implements IHANStack<T> {
    private HANLinkedList<T> list;

    public HANStack() {
        list = new HANLinkedList<>();
    }

    @Override
    public void push(T value) {
        list.addFirst(value);
    }

    @Override
    public T pop() {
        T value = list.getFirst();
        list.removeFirst();
        return value;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }
}