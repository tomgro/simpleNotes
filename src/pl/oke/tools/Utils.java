package pl.oke.tools;


public class Utils {
	public static boolean isEmpty(Object o) {
		if(o.toString().trim().equals("")) return true;
		return false;
	}
}
