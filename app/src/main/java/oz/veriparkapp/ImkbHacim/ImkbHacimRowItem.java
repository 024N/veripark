package oz.veriparkapp.ImkbHacim;

public class ImkbHacimRowItem {
	private String sembol;
	private String name;
	private String gain;
	private String fund;

	public ImkbHacimRowItem(String sembol, String name, String gain, String fund) {
		this.sembol = sembol;
		this.name = name;
		this.gain = gain;
		this.fund = fund;
	}

	public String getSembol() {
		return sembol;
	}

	public void setSembol(String sembol) {
		this.sembol = sembol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGain() {
		return gain;
	}

	public void setGain(String gain) {
		this.gain = gain;
	}

	public String getFund() {
		return fund;
	}

	public void setFund(String fund) {
		this.fund = fund;
	}
}