package com.lazetest;

import java.util.function.Function;

import static com.lazetest.Stream.*;
/**
 * Created by Jo_seungwan on 2017. 5. 2..
 */

public class Main {
    static Stream<Stream<Integer>> ants() {
        Function<Stream<Integer>, Stream<Integer>> f =
                line -> line.group().concatMap(g -> stream(g.head(), g.length()));
        return iterate(f, stream(1));
    }
    public static void main(String[] args) {
        System.out.println(ants().get(1000000).get(1000000));
    }
}
