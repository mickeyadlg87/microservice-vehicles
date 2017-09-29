
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import rx.Observable;

/**
 *
 * @author gaston
 */
public class RxTest {

	static class Msg {

		public List<String> value = new ArrayList<>();

		public List<String> getValue() {
			return value;
		}

		public Observable<List<String>> valueObservable() {
			return Observable.just(value);
		}

		public Observable<List<String>> valueObservableDefer() {
			return Observable.defer(() -> Observable.just(value));
		}
	}

	public static void main(String[] args) {
		justMethod();
		deferMethod();
		futureMethod();
	}

	/**
	 * No imprime nada en la consola, porque Observable.just se ejecuta cuando se crea el observable
	 */
	public static void justMethod() {
		Msg msg = new Msg();
		Observable<List<String>> obs = msg.valueObservable();
		msg.value = Arrays.asList("CHAU");
		obs.subscribe((List<String> s)
				-> s.forEach(m -> {
					System.out.println(s);
				}
				));
	}

	/**
	 * Con defer, se espera hasta que se suscriba alguien al observable, por lo que si muestra en 
	 * pantalla un resultado
	 */
	public static void deferMethod() {

		Msg msg = new Msg();
		Observable<List<String>> obs = msg.valueObservableDefer();
		msg.value = Arrays.asList("HOLA", "CHAU");
		obs.subscribe((List<String> s)
				-> s.forEach(m -> {
					System.out.println(s);
				}
				));
	}

	public static void futureMethod() {
		Observable<List<Integer>> obsFuture = Observable.from(CompletableFuture.supplyAsync(() -> {
			return Arrays.asList(1, 2, 3, 4, 5, 6, 7);
		}));

		obsFuture.subscribe((List<Integer> t) -> {
			t.forEach(v -> {
				System.out.println(v);
			});
		});
	}
}
