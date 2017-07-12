package it.uniroma3.model;


public class Image implements Comparable<Image>{
	
	private double score;
	private String imagePath;

	public Image(double score,String imagePath){
		this.score = score;
		this.imagePath = imagePath;
	}
	
	public double getScore() {
		return score;
	}

	public void setScore(double score,String imagePath) {
		this.score = score;
		this.imagePath = imagePath;
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@Override
	public int compareTo(Image i) {
		if(this.getScore() == i.getScore()){
			return this.hashCode() - i.hashCode();
			
		}
		
		else{
			if(i.getScore() > this.getScore()){
				return 1;
			}
			
			else{
				return -1;
			}
		}
	}
	
}