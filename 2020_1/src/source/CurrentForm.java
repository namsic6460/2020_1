package source;

@SuppressWarnings("serial")
public class CurrentForm extends Base {

	public CurrentForm() {
		super("form", 0, 0, 0, 0);
		
		this.addWindowListener(defaultWinListener);
	}
	
}
