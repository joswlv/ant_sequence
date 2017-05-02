package com.lazetest;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Jo_seungwan on 2017. 5. 2..
 */
abstract class Stream<T> {
    public final static Stream<Object> EMPTY = new Empty();

    static <T> Stream<T> cons(Supplier<T> head, Supplier<Stream<T>> tail) {
        return new Cons<>(head,tail);
    }
    static <T> Stream<T> empty(){
        return (Stream<T>) EMPTY;
    }
    static <T> Stream<T> stream(T... ts) {
        return stream_(0,ts);
    }

    private static <T> Stream<T> stream_(int i, T[] ts) {
        if (ts.length == i)
            return empty();
        return cons(() -> ts[i], () -> stream_(i+1,ts));
    }

    public static <T> Stream<T> iterate(Function<T,T> f, T t0) {
        return cons(() -> t0, () -> iterate(f, f.apply(t0)));
    }

    // concatMap :: Stream<A> -> (A -> Stream<B>) -> Stream<B>
    public <B> Stream<B> concatMap(Function<T, Stream<B>> f) {
        if (isEmpty())
            return empty();
        return f.apply(head()).append(() -> tail().concatMap(f));
    }

    private Stream<T> append(Supplier<Stream<T>> that) {
        if(isEmpty())
            return that.get();
        return cons(() -> head(), () -> tail().append(that));
    }

    public int length() {
        Stream<T> cur = this;
        int length = 0;
        while (!cur.isEmpty()) {
            cur = cur.tail();
            length++;
        }
        return length;
    }

    abstract public T head();
    abstract public boolean isEmpty();
    abstract Stream<T> tail();

    public void log() {
        Stream<T> cur = this;
        int count = 0;
        while (!cur.isEmpty() && count < 5){
            System.out.println(cur.head());
            cur = cur.tail();
            count++;
        }
    }

    // [1,1,1,1,1,2,2,....] -> [[1,1,1,1,1],...
    // group :: Stream<T> -> Stream<Stream<T>>
    public Stream<Stream<T>> group() {
        if (isEmpty())
            return empty();
        return cons(() -> this.takeWhile(x -> head().equals(x))
        , () -> this.dropWhile(x -> head().equals(x)).group());
    }

    public Stream<T> takeWhile(Predicate<T> f) {
        if (isEmpty())
            return empty();
        return f.test(head())
                ? cons(() -> head(), () -> tail().takeWhile(f))
                : empty();
    }

    public Stream<T> dropWhile(Predicate<T> f) {
        Stream<T> cur = this;
        while (!cur.isEmpty() && f.test(cur.head())){
            cur = cur.tail();
        }
        return cur;
    }

    public T get(int i) {
        return drop(i).head();
    }

    // drop :: Stream<T> -> Int -> Stream<T>
    public Stream<T> drop(int n) {
        Stream<T> cur = this;
        while (n-- > 0) {
            cur = cur.tail();
        }
        return cur;
    }

    static class Cons<T> extends Stream<T> {

        private Supplier<T> head;
        private Supplier<Stream<T>> tail;
        private T head_;
        private Stream<T> tail_;

        public Cons(Supplier<T> head, Supplier<Stream<T>> tail) {

            this.head = head;
            this.tail = tail;
        }

        @Override
        public T head() {
            if (head != null) {
                head_ = head.get();
                head = null;
            }
            return head_;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Stream<T> tail() {
            if (tail != null) {
                tail_ = tail.get();
                tail = null;
            }
            return tail_;
        }
    }

    static class Empty extends Stream<Object> {

        @Override
        public Object head() {
            throw new NoSuchElementException();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Stream<Object> tail() {
            throw new NoSuchElementException();
        }
    }
}
