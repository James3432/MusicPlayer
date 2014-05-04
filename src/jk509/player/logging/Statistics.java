package jk509.player.logging;

class Statistics {
	public String[] descriptors = { "value a", "value b" }; //TODO
	public double[] values = new double[descriptors.length];
	@Override public String toString(){ 
		String res = "";
		for(int i=0; i<descriptors.length; ++i)
			res += descriptors[i] + ": " + values[i] + "\n";
		return res;
	}
}