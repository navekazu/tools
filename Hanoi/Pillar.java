import java.util.ArrayList;
import java.util.List;

public class Pillar {
	private List<Plate> plates;

	public Pillar() {
		plates = new ArrayList<>();
	}

	public void stack(Plate plate) {
	}

	public Plate getTop() {
		if (plates.isEmpty()) {
			return null;
		}

		return plates.get(plates.size()-1);
	}

	public Plate putTop() {
		if (plates.isEmpty()) {
			return null;
		}

		Plate plate = plates.get(plates.size()-1);
		plates.remove(plates.size()-1);
		return plate;
	}
}
