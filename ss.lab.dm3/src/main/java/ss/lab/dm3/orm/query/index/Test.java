package ss.lab.dm3.orm.query.index;

/**
 * @author dmitry
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		TestIndex index = new TestIndex();
		index.put("one", "Hello");
		index.put("two", "Hello, ");
		index.put("two", "World");
		index.put("three", "Hello, ");
		index.put("three", "World");
		index.put("three", "!");

		final Collector collector = new Collector();
		index.collect("nothing", collector); // ""
		collector.println();
		index.collect("one", collector); // Hello
		collector.println();
		index.collect("two", collector); // Hello, World
		collector.println();
		index.collect("three", collector); // Hello, World!
		collector.println();
	}

}

class TestIndex {

	Map<String, List<String>> keyToValues = new HashMap<String, List<String>>();

	public void put(String key, String value) {
		
		List<String> items = this.keyToValues.get(key);
		if (items == null) {
			items = new ArrayList<String>();
			this.keyToValues.put(key, items);
		}
		items.add(value);	
		System.out.println("key to value " + this.keyToValues.get(key));
	}

	public void collect(String key, Collector collector) {
		List<String> items = this.keyToValues.get(key);
		if (items != null) {
			for (String value : items) {
				collector.add(value);

			}
		}
	}

}

class Collector {

	public void add(String value) {
		System.out.print(value);
	}

	/**
	 * 
	 */
	public void println() {
		System.out.println();
	}

}
