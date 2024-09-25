package nl.han.ica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HANLinkedListTest {
        private HANLinkedList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new HANLinkedList<>();
        }

        @Test
        void addFirst() {
            list.addFirst(1);
            assertEquals(1, list.getFirst());
        }

        @Test
        void clear() {
            list.addFirst(1);
            list.clear();
            assertEquals(0, list.getSize());
        }

        @Test
        void insert() {
            list.insert(0, 1);
            assertEquals(1, list.get(0));
        }

        @Test
        void delete() {
            list.addFirst(1);
            list.delete(0);
            assertEquals(0, list.getSize());
        }

        @Test
        void get() {
            list.addFirst(1);
            assertEquals(1, list.get(0));
        }

        @Test
        void removeFirst() {
            list.addFirst(1);
            list.removeFirst();
            assertEquals(0, list.getSize());
        }

        @Test
        void getFirst() {
            list.addFirst(1);
            assertEquals(1, list.getFirst());
        }

        @Test
        void getSize() {
            list.addFirst(1);
            assertEquals(1, list.getSize());
        }

        @Test
        void iterator() {
            list.addFirst(1);
            assertTrue(list.iterator().hasNext());
            assertEquals(1, list.iterator().next());
        }
    }
