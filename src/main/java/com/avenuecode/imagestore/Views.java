package com.avenuecode.imagestore;

public class Views {
	public interface Basic {}
	
	public interface Images extends Basic {}
	
	public interface Products extends Basic {}
	
	public interface All extends Images, Products {}
	
	/**
	 * This utility method returns the appropriate view Class based in the values of the
	 * two parameters given.
	 * @param loadChildProducts
	 * @param loadChildImages
	 * @return Views.All.class if both parameters are true. Views.Images.class if only loadChildImages is true.
	 * Views.Products.class if only loadChildProducts is true, and Views.Basic.class if both parameters are false.
	 */
	public static Class<? extends Views.Basic> chooseViewFor(boolean loadChildProducts, boolean loadChildImages) {
		
		if (loadChildImages && loadChildProducts) {
			return Views.All.class;
		} else if (loadChildImages) {
			return Views.Images.class;
		} else if (loadChildProducts) {
			return Views.Products.class;
		} 	
		return Views.Basic.class;				
	}	
}
