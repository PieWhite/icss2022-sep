package nl.han.ica;

import nl.han.ica.datastructures.IHANQueue;

public class HANQueue <T> implements IHANQueue<T> {

    private HANLinkedList<T> list;

    public HANQueue() {
        list = new HANLinkedList<>();
    }
    @Override
    public void enqueue(T value) {
        list.addFirst(value);
    }

    @Override
    public T dequeue() {
        if (list.getSize() == 0) {
            return null;
        }
        T value = list.get(list.getSize() - 1);
        list.delete(list.getSize() - 1);
        return value;
    }

    @Override
    public T peek() {
        if (list.getSize() == 0) {
            return null;
        }
        return list.get(list.getSize() - 1);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean isEmpty() {
        return list.getSize() == 0;
    }

    @Override
    public int getSize() {
        return list.getSize();
    }
}
