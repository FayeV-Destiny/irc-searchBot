package com.destiny.irc.bot.response;

import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Created by eric.tournier on 29/04/2016.
 */
public class IrcResponses implements List<IrcResponseLine> {
    private List<IrcResponseLine> lines;

    public IrcResponses(List<XdmNode> nodes) {
        super();
        lines = new ArrayList<>(nodes.size());
        for (XdmNode node : nodes) {
            lines.add(new IrcResponseLine(node));
        }
    }

    @Override
    public int size() {
        return lines.size();
    }

    @Override
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return lines.contains(o);
    }

    @Override
    public Iterator<IrcResponseLine> iterator() {
        return lines.iterator();
    }

    @Override
    public Object[] toArray() {
        return lines.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return lines.toArray(a);
    }

    @Override
    public boolean add(IrcResponseLine ircResponseLine) {
        return lines.add(ircResponseLine);
    }

    @Override
    public boolean remove(Object o) {
        return lines.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return lines.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IrcResponseLine> c) {
        return lines.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends IrcResponseLine> c) {
        return lines.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return lines.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return lines.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<IrcResponseLine> operator) {
        lines.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super IrcResponseLine> c) {
        lines.sort(c);
    }

    @Override
    public void clear() {
        lines.clear();
    }

    @Override
    public boolean equals(Object o) {
        return lines.equals(o);
    }

    @Override
    public int hashCode() {
        return lines.hashCode();
    }

    @Override
    public IrcResponseLine get(int index) {
        return lines.get(index);
    }

    @Override
    public IrcResponseLine set(int index, IrcResponseLine element) {
        return lines.set(index, element);
    }

    @Override
    public void add(int index, IrcResponseLine element) {
        lines.add(index, element);
    }

    @Override
    public IrcResponseLine remove(int index) {
        return lines.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return lines.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return lines.lastIndexOf(o);
    }

    @Override
    public ListIterator<IrcResponseLine> listIterator() {
        return lines.listIterator();
    }

    @Override
    public ListIterator<IrcResponseLine> listIterator(int index) {
        return lines.listIterator(index);
    }

    @Override
    public List<IrcResponseLine> subList(int fromIndex, int toIndex) {
        return lines.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<IrcResponseLine> spliterator() {
        return lines.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super IrcResponseLine> filter) {
        return lines.removeIf(filter);
    }

    @Override
    public Stream<IrcResponseLine> stream() {
        return lines.stream();
    }

    @Override
    public Stream<IrcResponseLine> parallelStream() {
        return lines.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super IrcResponseLine> action) {
        lines.forEach(action);
    }
}
