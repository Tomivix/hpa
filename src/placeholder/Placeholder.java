package placeholder;

public class Placeholder {
	public static int[][] getIndexes(String s){
		return new int[][]{
			new int[]{
					0, s.length()/3
			},
			new int[]{
					s.length()-4, 2
			}
		};
	}
	public static int[][] getIndexes2(String s){
		return new int[][]{
			new int[]{
					3, s.length()/5
			},
			new int[]{
					s.length()*4/5, 5
			}
		};
	}

}
