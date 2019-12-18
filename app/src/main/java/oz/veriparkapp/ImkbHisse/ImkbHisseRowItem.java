package oz.veriparkapp.ImkbHisse;

public class ImkbHisseRowItem {
	private String sembol;
	private String fiyat;
	private String fark;
	private String islemHacmi;
	private String alis;
	private String satis;
	private String saat;

	public ImkbHisseRowItem(String sembol, String fiyat, String fark, String islemHacmi, String alis, String satis, String saat) {
		this.sembol = sembol;
		this.fiyat = fiyat;
		this.fark = fark;
		this.islemHacmi = islemHacmi;
		this.alis = alis;
		this.satis = satis;
		this.saat = saat;
	}

	public String getSembol() {
		return sembol;
	}

	public void setSembol(String sembol) {
		this.sembol = sembol;
	}

	public String getFiyat() {
		return fiyat;
	}

	public void setFiyat(String fiyat) {
		this.fiyat = fiyat;
	}

	public String getFark() {
		return fark;
	}

	public void setFark(String fark) {
		this.fark = fark;
	}

	public String getIslemHacmi() {
		return islemHacmi;
	}

	public void setIslemHacmi(String islemHacmi) {
		this.islemHacmi = islemHacmi;
	}

	public String getAlis() {
		return alis;
	}

	public void setAlis(String alis) {
		this.alis = alis;
	}

	public String getSatis() {
		return satis;
	}

	public void setSatis(String satis) {
		this.satis = satis;
	}

	public String getSaat() {
		return saat;
	}

	public void setSaat(String saat) {
		this.saat = saat;
	}
}