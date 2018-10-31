package cs2030.mystream;

import java.util.function.UnaryOperator;
import java.util.function.Supplier;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.Optional;

public interface InfiniteList<T> {

	public static <T> InfiniteList<T> generate(Supplier<T> supplier) {
		//return new IFL<T>(supplier,() -> new IFL(supplier.get(),));
		return IFL.generate(supplier);
	}

	public static <T> InfiniteList<T> iterate(T seed,UnaryOperator<T> next) {
		//return new IFL<T>(() -> seed,() -> next.apply(seed));
		return IFL.iterate(seed,next); 
	}

	public long count();

	public void forEach(Consumer<? super T> action);

	public Optional<T> reduce(BinaryOperator<T> accumulator); 
	
	public T reduce(T identity, BinaryOperator<T> accumulator);

	public Object[] toArray();

	public InfiniteList<T> limit(long maxzize);

	public InfiniteList<T> filter(Predicate<T> predicate);

	public <R> InfiniteList<R> map(Function<T,R> mapper);

	public InfiniteList<T> takeWhile(Predicate<T> predicate);

}
