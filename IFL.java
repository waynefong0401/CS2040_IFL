package cs2030.mystream;

import java.util.function.Supplier;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class IFL<T> implements InfiniteList<T> {
    private Supplier<T> head;
    private Supplier<IFL<T>> tail;
    private Optional<Predicate<T>> p;

    protected IFL() {}

    /**
     * Constructor of IFL class.
     * @param s Supplier of head
     * @param next Supplier of next IFL
     */
    IFL(Supplier<T> s, Supplier<IFL<T>> next) {
        this.head = s;
        this.tail = next;
    }

    /**
     * Get the count of stream.
     * @return long of count of stream
     */
    public long count() {
        long count = 0L;
        IFL<T> list = this;

        while (!list.isEmpty()) {
            count++;
            list = list.tail.get();
        }
        return count;
    }

    /**
     * Performs an action for each element of this stream.
     * @param action the action
     */
    public void forEach(Consumer<? super T> action) {
        IFL<T> list = this;

        while (!list.isEmpty()) {
            action.accept(list.head.get());
            list = list.tail.get();
        }
    }

    /**
     * Perfrom reduction on elements of IFL using accumulator.
     * @param accumulator the accumulator for reduction
     * @return optional result of the reduction
     */
    public Optional<T> reduce(BinaryOperator<T> accumulator) {
        boolean foundAny = false;
        T result = null;
        IFL<T> list = this;

        while (!list.isEmpty()) {
            if (!foundAny) {
                foundAny = true;
                result = list.head.get();
                list = list.tail.get();
            } else {
                result = accumulator.apply(result, list.head.get());
                list = list.tail.get();
            }
        }
        return foundAny ? Optional.of(result) : Optional.empty();
    }

    /**
     * Perfrom reduction on elements of IFL using accumulator.
     * @param accumulator the accumulator for reduction
     * @return result of the reduction
     */
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        IFL<T> list = this;

        while (!list.isEmpty()) {
            result = accumulator.apply(result, list.head.get());
            list = list.tail.get();
        }
        return result;
    }

    /**
     * Returns an array containing the elements of this stream.
     * @return an array containing the elements in this stream
     */
    public Object[] toArray() {
        List<Object> result = new ArrayList<>();
        IFL<T> list = this;

        while (!list.isEmpty()) {
            if (list.head != null) {
                result.add(list.head.get());
            }
            list = list.tail.get();
        }
        return result.toArray();
    }

    /**
     * Limit tha max number of IFL.
     * @param n max number
     * @return IFL with limit
     */
    public IFL<T> limit(long n) {
        if (n == 0 || isEmpty()) {
            return new EmptyList<T>();
        }
        if (n > 1) {
            return new IFL<T>(head, () -> tail.get().limit(n - 1));
        } else {
            return new IFL<T>(head, () -> new EmptyList<T>());
        }
    }

    /**
     * Filter the IFL.
     * @param p filter
     * @return IFL with filter prediactate
     */
    public IFL<T> filter(Predicate<T> p) {
        IFL<T> list = this;
        if (list.isEmpty()) {
            return new EmptyList<T>();
        }
        return getNextFiltered(p,list);
    }

    private IFL<T> getNextFiltered(Predicate<T> p,IFL<T> list) {
        if (list.tail.get().isEmpty()) {
            return new EmptyList<T>();
        }
        if (p.test(list.head.get())) {
            return new IFL<T>(list.head, () -> list.tail.get().filter(p));
        } else {
            return getNextFiltered(p,list.tail.get());
        }

    }

    /**
     * Map the IFL.
     * @param mapper mapper
     * @return IFL with type R
     */
    public <R> IFL<R> map(Function<T,R> mapper) {
        if (isEmpty()) {
            return new EmptyList<R>();
        }
        return new IFL<R>(() -> mapper.apply(head.get()),
                () -> tail.get().map(mapper));
    }

    /**
     * Stop the IFL when element does not satisfy the predicate.
     * @param predicate the condition
     * @return IFL with the constrain
     */
    public IFL<T> takeWhile(Predicate<T> predicate) {
        if (isHeadFiltered()) {

        }
        return new IFL<T>(head,
                () -> {
                    return tail.get().takeWhile(predicate);
                }) {
            @Override
            boolean isEmpty() {
                if (predicate.test(head.get())) {
                    return false;
                } else {
                    return true;
                }
            }
        };
    }

    boolean isEmpty() {
        return false;
    }

    boolean isHeadFiltered() {
        if (p.isPresent()) {
            if (p.get().test(head.get())) {
                return false;
            }
        }
        return false;
    }

    public static <U> IFL<U> generate(Supplier<U> s) {
        return new IFL<U>(s,() -> generate(s));
    }

    public static <U> IFL<U> iterate(U seed, Function<U,U> next) {
        return new IFL<U>(() -> seed,() -> iterate(next.apply(seed), next));
    }

    @Override
    public String toString() {
        return head.get().toString() + tail.get().toString();
    }
}
