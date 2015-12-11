package org.paces.Stata.ColorTerp;

import com.stata.sfi.Macro;
import javafx.scene.paint.Color;

/***
 * Class and methods used for color interpolation in Stata.
 * @author Billy Buchanan
 * @version 0.0.0
 * @date 11dec2015
 */
public class ColorTerp {

	/***
	 * Boolean used to return arbitrarily brighter color
	 */
	private boolean brighter;

	/***
	 * Boolean used to return arbitrarily darker color
	 */
	private boolean darker;

	/***
	 * Boolean used to return arbitrarily more saturated color
	 */
	private boolean saturated;

	/***
	 * Boolean used to return arbitrarily less saturated color
	 */
	private boolean desaturated;

	/***
	 * Boolean for inverting colors
	 */
	private boolean invertcolors;

	/***
	 * Returned Color space indicator
	 */
	private String retspace;

	/***
	 * Input color space
	 */
	private String inspace;

	/***
	 * Color object for Starting Color
	 */
	private Color start;

	/***
	 * Color object for ending color
	 */
	private Color end;

	/***
	 * Number of points between start and end to interpolate
	 */
	private int points;

	/***
	 * An array of doubles with the proportional distance values from start
	 * to end.
	 */
	private double[] dists;

	/***
	 * A string array containing space delimited RGB values of interpolated
	 * colors
	 */
	private String[] tcolors;

	/***
	 * Method used to construct a color object given an array of integer
	 * values and a user specified color space
	 * @param colors An array of integer color values
	 * @param inspace RGB or RGBA only
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(int[] colors, String inspace) {

		// Initialize a color object
		Color x;

		// If user specified RGB color space
		if (colors.length == 3 && inspace.equals("rgb")) {

			// Populate object with Color defined by the rgb elements of the
			// int array
			x = Color.rgb(colors[0], colors[1], colors[2]);

		// If there are four int[] elements and colorspace is rgba
		} else if (colors.length == 4 & inspace.equals("rgba")) {

			// Set rgb from first three elements of array and alpha parameter
			// from the fourth element of the array
			x = Color.rgb(colors[0], colors[1], colors[2], colors[3]);

		// Should not be reached
		} else {

			// Default value to prevent compiler from crying
			x = Color.STEELBLUE;

		} // End IF/ELSE Block for color handling

		// Return the color object
		return x;

	} // End of Method declaration

	/***
	 * Method used to construct a color object given an array of double
	 * values and a user specified color space
	 * @param colors An array of double valued color components
	 * @param inspace sRGB, sRGBa, HSB, or HSBa
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(double[] colors, String inspace) {

		// Initialize a color object
		Color x;

		// If user specified sRGB color space and the double[] has 3 elements
		if (colors.length == 3 && inspace.equals("srgb")) {

			// Use the 3 array elements to define the rgb properties in sRGB
			x = Color.color(colors[0], colors[1], colors[2]);

		// If user specified sRGBa color space and the double[] has 4 elements
		} else if (colors.length == 4 & inspace.equals("srgba")) {

			// First 3 elements are rgb, 4th is alpha
			x = Color.color(colors[0], colors[1], colors[2], colors[3]);

		// If user specified sRGB color space and the double[] has 3 elements
		} else if (colors.length == 3 && inspace.equals("hsb")) {

			// Use the 3 array elements to define the hsb properties in HSB
			x = Color.hsb(colors[0], colors[1], colors[2]);

		// If user specified sRGB color space and the double[] has 3 elements
		} else if (colors.length == 4 && inspace.equals("hsba")) {

			// First 3 elements are hsb, 4th is alpha
			x = Color.hsb(colors[0], colors[1], colors[2], colors[3]);

		// Should not be reached
		} else {

			// Default value to prevent compiler from crying
			x = Color.STEELBLUE;

		} // End IF/ELSE Block for color handling

		// Return the color object
		return x;

	} // End of Method declaration


	/***
	 * Method used to construct a color object given an array of integer
	 * values and a user specified color space
	 * @param colors An array of String with one or two elements
	 * @param inspace web only
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(String[] colors, String inspace) {

		// Initialize a color object
		Color x;

		// Check if there is only a single value in the array
		if (colors.length <= 1 && inspace == "web") {

			// Process the web formatted color
			x = Color.web(colors[0]);

		// Otherwise
		} else {

			// Second argument would be for alpha transparency
			x = Color.web(colors[0], Double.valueOf(colors[1]));

		} // End ELSE Block

		// Return the color object
		return x;

	} // End of Method declaration

	/***
	 * Setter method for the input color space
	 */
	public void setInSpace() {

		// Sets the input color space variable
		this.inspace = Macro.getLocalSafe("icspace");

	} // End of setter for input color space

	/***
	 * Setter method for the returned color space
	 */
	public void setRetSpace() {

		// Sets the return value color space
		this.retspace = Macro.getLocalSafe("rcspace");

	} // End of setter for return value color space

	/***
	 * Setter method for inverted colors
	 */
	public void setInverse() {

		// Set inverted colors to boolean version of string
		this.invertcolors = Boolean.valueOf(Macro.getLocalSafe("inverse"));

	} // End of setter for inverse colors

	/***
	 * Setter method for brightness, darkness, saturated, and/or desaturated
	 * variables
	 * @param colorspace The output colorspace used for the color interpolation
	 */
	public void setBrighter(String colorspace) {

		// Gets the luminance value from Stata
		String b = Macro.getLocalSafe("luminance");

		// For all but the HSB based color spaces
		if (colorspace.equals("rgb") || colorspace.equals("rgba") ||
			colorspace.equals("srgb") || colorspace.equals("srgba") ||
			colorspace.equals("web")) {

			// For brighter set brighter true and all else false
			if (b.equals("brighter")) {
				this.brighter = true;
				this.darker = false;
				this.saturated = false;
				this.desaturated = false;

			// For darker set darker true and all else false
			} else if (b.equals("darker")) {
				this.brighter = false;
				this.darker = true;
				this.saturated = false;
				this.desaturated = false;

			// For all other cases set all to false
			} else {
				this.brighter = false;
				this.darker = false;
				this.saturated = false;
				this.desaturated = false;
			}

		// For HSB based color spaces
		} else {

			// For brighter set saturated to true
			if (b.equals("brighter")) {
				this.brighter = false;
				this.darker = false;
				this.saturated = true;
				this.desaturated = false;

			// For darker set desaturated to true
			} else if (b.equals("darker")) {
				this.brighter = false;
				this.darker = false;
				this.saturated = false;
				this.desaturated = true;

			// For every other case set everything to false
			} else {
				this.brighter = false;
				this.darker = false;
				this.saturated = false;
				this.desaturated = false;

			} // End IFELSE Block for HSB parameters

		} // End ELSE Block for HSB color space

	} // End Method declaration

	/***
	 * Method to parse string arguments into array for color definitions
	 * @param values A string array of values used to define elements of the
	 *                  color
	 * @return An array of doubles and length 3 for sRGB and HSB
	 */
	public double[] parseColor(String[] values) {

		// Initialize array of doubles with length 3
		double[] cols = new double[3];

		// Loop over the values from 0 - 2
		for(int i = 0; i < values.length; i++) {

			// Add the ith element to the array by recasting the string to a
			// double value
			cols[i] = Double.valueOf(values[i]);

		} // End of Loop

		// Return the array object
		return cols;

	} // End of Method declaration


	/***
	 * Method to parse string arguments into array for color definitions
	 * @param values A string array of values used to define elements of the
	 *                  color
	 * @return An array of doubles and length 4 for sRGBa and HSBa
	 */
	public double[] parseColorAlpha(String[] values) {

		// Initialize array of doubles with length 4
		double[] cols = new double[4];

		// Loop over the values from 0 - 3
		for(int i = 0; i < values.length; i++) {

			// Add the ith element to the array by recasting the string to a
			// double value
			cols[i] = Double.valueOf(values[i]);

		} // End of Loop

		// Return the array object
		return cols;

	}

	/***
	 * Method to parse string arguments into array for color definitions
	 * @param values A string array of values used to define elements of the
	 *                  color
	 * @return An array of ints and length 3 for RGB
	 */
	public int[] parseRGB(String[] values) {

		// Initialize array of doubles with length 3
		int[] cols = new int[3];

		// Loop over the values from 0 - 2
		for(int i = 0; i < values.length; i++) {

			// Add the ith element to the array by recasting the string to an
			// int value
			cols[i] = Integer.valueOf(values[i]);

		} // End of Loop

		// Return the array object
		return cols;

	} // End of Method declaration

	/***
	 * Method to parse string arguments into array for color definitions
	 * @param values A string array of values used to define elements of the
	 *                  color
	 * @return An array of ints and length 4 for RGBa
	 */
	public int[] parseRGBAlpha(String[] values) {

		// Initialize array of doubles with length 4
		int[] cols = new int[4];

		// Loop over the values from 0 - 3
		for(int i = 0; i < values.length; i++) {

			// Add the ith element to the array by recasting the string to an
			// int value
			cols[i] = Integer.valueOf(values[i]);

		} // End of Loop

		// Return the array object
		return cols;

	} // End of Method declaration

	/***
	 * Method that returns a color object from an RGB String value
	 * @param sc A comma delimited string of RGB values for a single color
	 * @param ispace The input color space used to define the color values in
	 *                  the string parameter sc.
	 */
	public void setStart(String sc, String ispace) {

		// Parses the passed string based on spaces
		String[] vals = sc.split(" ");

		// Dispatches appropriate method for input color space and color def
		switch (ispace) {

			// For RGB Colorspace
			case "rgb" :

				// Set the Starting Color object
				this.start = setColor(parseRGB(vals), ispace);

				// Break out of the switch
				break;

			// For RGBa Colorspace
			case "rgba" :

				// Set the Starting Color object
				this.start = setColor(parseRGBAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For sRGB Colorspace
			case "srgb" :

				// Set the Starting Color object
				this.start = setColor(parseColor(vals), ispace);

				// Break out of the switch
				break;

			// For HSB Colorspace
			case "hsb" :

				// Set the Starting Color object
				this.start = setColor(parseColor(vals), ispace);

				// Break out of the switch
				break;

			// For sRGBa Colorspace
			case "srgba" :

				// Set the Starting Color object
				this.start = setColor(parseColorAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For HSBa Colorspace
			case "hsba" :

				// Set the Starting Color object
				this.start = setColor(parseColorAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For Web Colorspace
			default :

				// Set the Starting Color object
				this.start = setColor(vals, ispace);

				// Break out of the switch
				break;

		} // End Switch

	} // End Method declaration for starting color

	/***
	 * Method that returns a color object from an RGB String value
	 * @param ec A comma delimited string of RGB values for a single color
	 * @param ispace The input color space used to define the color values in
	 *                  the string parameter sc.
	 */
	public void setEnd(String ec, String ispace) {

		// Parses the passed string based on spaces
		String[] vals = ec.split(" ");

		// Dispatches appropriate method for input color space and color def
		switch (ispace) {

			// For RGB Colorspace
			case "rgb" :

				// Set the ending Color object
				this.end = setColor(parseRGB(vals), ispace);

				// Break out of the switch
				break;

			// For RGBa Colorspace
			case "rgba" :

				// Set the ending Color object
				this.end = setColor(parseRGBAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For sRGB Colorspace
			case "srgb" :

				// Set the ending Color object
				this.end = setColor(parseColor(vals), ispace);

				// Break out of the switch
				break;

			// For HSB Colorspace
			case "hsb" :

				// Set the ending Color object
				this.end = setColor(parseColor(vals), ispace);

				// Break out of the switch
				break;

			// For sRGBa Colorspace
			case "srgba" :

				// Set the ending Color object
				this.end = setColor(parseColorAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For HSBa Colorspace
			case "hsba" :

				// Set the ending Color object
				this.end = setColor(parseColorAlpha(vals), ispace);

				// Break out of the switch
				break;

			// For Web Colorspace
			default :

				// Set the ending Color object
				this.end = setColor(vals, ispace);

				// Break out of the switch
				break;

		} // End Switch

	} // End Method declaration for ending color

	/***
	 * Method to define the distances between the start and end colors
	 * @param points The number of colors to interpolate between start and end
	 */
	public void setDistances(int points) {

		// Temporary object to store the distances
		double[] tmp = new double[points];

		// For arrays that are too short
		if (points == 1) {

		} else {

			// Get the fraction between start and end for a # of points
			double intdist = 1.0 / points;

			// Distance starts at zero
			double totdist = 0.0;

			// Loop over the number of points
			for(int i = 0; i < points; i++) {

				// Add the intdist interval to the totdist object
				totdist = totdist + intdist;

				// Add each of the updated values to the array
				tmp[i] = totdist;

			} // End Loop

		} // End ELSE Block

		// Return the double array object
		this.dists = tmp;

	} // End of method declaration

	/***
	 * Method to create a string array of RGB values given start and end colors
	 * and the distance between them.
	 * @param s Starting color object
	 * @param e Ending color object
	 * @param distances Array of distances between start and end
	 */
	public void setTColors(Color s, Color e, double[] distances) {

		// Sets up storage object
		String[] colors = new String[distances.length];

		// Loop over the distances
		for(int i = 0; i < distances.length; i++) {

			// Interpolate the color between the start and end for a given distance
			Color tmpColor = s.interpolate(e, distances[i]);

			// Get String value of the red color rounded to nearest integer
			String r = String.valueOf(Math.round(tmpColor.getRed() * 255));

			// Get String value of the green color rounded to nearest integer
			String g = String.valueOf(Math.round(tmpColor.getGreen() * 255));

			// Get String value of the blue color rounded to nearest integer
			String b = String.valueOf(Math.round(tmpColor.getBlue() * 255));

			// Add the space delimited string as an array element
			colors[i] = r + " " + g + " " + b;

		} // End Loop over the distance array

		// Set the string array object that holds the RGB values
		this.tcolors = colors;

	} // End of method definition

	/***
	 * Method to translate color object into a color string
	 * @param thecolor The interpolated color object
	 * @param cspace The return color space
	 * @return A space delimited string of component values for the
	 * interpolated colors.
	 */
	public String getColorString(Color thecolor, String cspace) {

		// Initialize the return string
		String color;

		// For RGB-based color spaces
		if (cspace.equals("rgb") || cspace.equals("rgba")) {

			// Get String value of the red color rounded to nearest integer
			String r = String.valueOf(Math.round(thecolor.getRed() * 255));

			// Get String value of the green color rounded to nearest integer
			String g = String.valueOf(Math.round(thecolor.getGreen() * 255));

			// Get String value of the blue color rounded to nearest integer
			String b = String.valueOf(Math.round(thecolor.getBlue() * 255));

			// For RGB with alpha transparency
			if (cspace.equals("rgba")) {

				// Retrieve the opacity parameter
				String a = String.valueOf(Math.round(thecolor.getOpacity() * 255));

				// Add the space delimited string as an array element
				color = r + " " + g + " " + b + " " + a;

			// For RGB without alpha transparency
			} else {

				// Add the space delimited string as an array element
				color = r + " " + g + " " + b;

			} // End ELSE Block for RGB

		// For sRGB and sRGBa colorspaces
		} else if (cspace.equals("srgb") || cspace.equals("srgba")) {

			// Get String value of the red color rounded to nearest integer
			String r = String.valueOf(thecolor.getRed());

			// Get String value of the green color rounded to nearest integer
			String g = String.valueOf(thecolor.getGreen());

			// Get String value of the blue color rounded to nearest integer
			String b = String.valueOf(thecolor.getBlue());

			// If colorspace includes alpha layer transparency
			if (cspace.equals("rgba")) {

				// Retrieve the opacity parameter
				String a = String.valueOf(thecolor.getOpacity());

				// Add the space delimited string as an array element
				color = r + " " + g + " " + b + " " + a;

			// For sRGB without alpha transparency
			} else {

				// Add the space delimited string as an array element
				color = r + " " + g + " " + b;

			} // End ELSE Block for sRGB

		// For HSB-based return color spaces
		} else {

			// Get String value of the hue
			String h = String.valueOf(thecolor.getHue());

			// Get String value of the saturation
			String s = String.valueOf(thecolor.getSaturation());

			// Get String value of the brightness
			String b = String.valueOf(thecolor.getBrightness());

			// Add the space delimited string as an array element
			color = h + " " + s + " " + b;

			// If colorspace includes alpha layer transparency
		 	if (cspace.equals("hsba")) {

				// Retrieve the opacity parameter
				String a = String.valueOf(thecolor.getOpacity());

				// Add the space delimited string as an array element
				color = h + " " + s + " " + b + " " + a;

			// For HSB without alpha transparency
			} else {

				// Add the space delimited string as an array element
				color = h + " " + s + " " + b;

			} // End ELSE Block for HSB w/o alpha transparency

		} // End ELSE Block for HSB-based color spaces

		// Return the color string
		return color;

	} // End of Method declaration

	/***
	 * Method to create a string array of RGB values given start and end colors
	 * and the distance between them.
	 * @param s Starting color object
	 * @param e Ending color object
	 * @param distances Array of distances between start and end
	 * @param brighter A Boolean used to return brighter versions of
	 *                    interpolated colors
	 * @param darker A boolean used to return arbitrarily darker version of the
	 *               interpolated colors
	 * @param saturated A boolean used to return arbitrarily more saturated
	 *                     version of the interpolated colors
	 * @param desaturated A boolean used to return arbitrarily less saturated
	 *                       version of the interpolated colors
	 * @param invert A boolean used to return the inverse of the interpolated
	 *                  colors
	 * @param cspace The return colorspace to use for the interpolated colors
	 */
	public void setTColors(Color s, Color e, double[] distances, boolean
			brighter, boolean darker, boolean saturated,
			boolean desaturated, boolean invert, String cspace) {

		// Sets up storage object
		String[] colors = new String[distances.length];

		// Loop over the distances
		for(int i = 0; i < distances.length; i++) {

			// Interpolate the color between the start and end for a given distance
			Color tmpColor = s.interpolate(e, distances[i]);

			// Adjust brightness of the colors
			if (brighter == true) {

				// Make color arbitrarily darker
				tmpColor = tmpColor.brighter();

			} // End IF Block for brighter color

			// Check for darker boolean
			if (darker == true) {

				// Make color arbitrarily darker
				tmpColor = tmpColor.darker();

			} // End IF Block for darker color

			// Check for saturated boolean
			if (saturated == true) {

				// Make color arbitrarily saturated
				tmpColor = tmpColor.saturate();

			} // End IF Block for saturated color

			// Check for desaturated boolean
			if (desaturated == true) {

				// Make color arbitrarily desaturated
				tmpColor = tmpColor.desaturate();

			} // End IF Block for desaturated color

			// Check for inverted color boolean
			if (invert == true) {

				// Get the inverse of the current color
				tmpColor = tmpColor.invert();

			} // End IF Block for inverse color

			// Store the color string in the ith array element
			colors[i] = getColorString(tmpColor, cspace);

		} // End Loop over the distance array

		// Set the string array object that holds the RGB values
		this.tcolors = colors;

	} // End of Method declaration for brighter colors

	/***
	 * Method to set the points member variable of the class
 	 * @param ptmacro A string passed to the method from Macro.getLocalSafe
	 *                   ("icolors");
	 */
	public void setPoints(String ptmacro) {

		// Sets value of points to the integer translation of the passed string
		this.points = Integer.valueOf(ptmacro);

	} // End of Method declaration


	/***
	 * Generic constructor method
	 * @param args Array of string arguments...not used.
	 */
	public ColorTerp(String[] args) {

		// Set the input color space
		setInSpace();

		// Set the return color space
		setRetSpace();

		// Starting Color
		setStart(Macro.getLocalSafe("scolor"), getInSpace());
		
		// Ending Color
		setEnd(Macro.getLocalSafe("ecolor"), getInSpace());

		// Number of colors to interpret between start and end
		setPoints(Macro.getLocalSafe("icolors"));
		
		// Set an array of doubles containing the distances between start and 
		// end colors
		setDistances(getPoints());
		
		// Get interpolated colors
		setTColors(getStart(), getEnd(), getDists(), getBrighter(),
				getDarker(), getInvertColors(), getSaturated(), getDesaturated(),
				getRetSpace());
		
	} // End Constructor method

	/***
	 * Constructor method for ColorTerp class
	 * @param args Arguments passed to the ColorTerp constructor (not used).
	 * @return An object of class ColorTerp
	 */
	public static int interpcolors(String[] args) {

		// Create a new ColorTerp object
		ColorTerp theColors = new ColorTerp(args);

		// Get the string array of interpolated colors
		String[] interpedColors = theColors.getTColors();

		// Return the interpolated colors
		theColors.toStata(interpedColors);

		// Return success code
		return 0;

	} // End Constructor method

	/***
	 * Getter method to access the points member variable
	 * @return An integer with the number of points to interpolate between
	 */
	public int getPoints() {

		// Returns the points variable
		return this.points;

	} // End method declaration for accessing points

	/***
	 * Getter method to access the points member variable
	 * @return An integer with the number of points to interpolate between
	 */
	public double[] getDists() {

		// Returns the distances between starting and ending colors
		return this.dists;

	} // End method declaration for accessing distance array

	/***
	 * Getter method to access the interpolated color values
	 * @return A string array with interpolated RGB values
	 */
	public String[] getTColors() {

		// Returns the string array object with the RGB values
		return this.tcolors;

	} // End of method declaration to get rgb values of interpolated colors

	/***
	 * Getter method to access the starting color variable
	 * @return An object of class Color that is translated from passed RGB
	 * values
	 */
	public Color getStart() {

		// Returns the starting color object
		return this.start;

	} // End of method declaration to get starting color object

	/***
	 * Getter method to access the ending color variable
	 * @return An object of class Color that is translated from passed RGB
	 * values
	 */
	public Color getEnd() {

		// Returns the ending color object
		return this.end;

	} // End of the method declaration to get ending color object

	/***
	 * Getter method for the Brightness parameter
	 * @return A boolean indicating whether or not to return an arbitrarily
	 * brighter color object.
	 */
	public boolean getBrighter() {

		// Returns boolean indicating if color should be arbitrarily brighted
		return this.brighter;

	} // End of Method declaration

	/***
	 * Getter method for the saturation parameter
	 * @return A boolean indicating whether or not to return an arbitrarily
	 * saturated color object.
	 */
	public boolean getSaturated() {

		// Returns boolean indicating if color should be arbitrarily more
		// saturated
		return this.saturated;

	} // End of Method declaration

	/***
	 * Getter method for the desaturation parameter
	 * @return A boolean indicating whether or not to return an arbitrarily
	 * desaturated color object.
	 */
	public boolean getDesaturated() {

		// Returns boolean indicating if color should be arbitrarily less
		// saturated
		return this.desaturated;

	} // End of Method declaration

	/***
	 * Getter method for the darker parameter
	 * @return A boolean indicating whether or not to return an arbitrarily
	 * darker color object.
	 */
	public boolean getDarker() {

		// Returns boolean indicating if color should be arbitrarily darker
		return this.darker;

	} // End of Method declaration

	/***
	 * Getter method for the inverse color parameter
	 * @return A boolean indicating whether or not to return the inverse color.
	 */
	public boolean getInvertColors() {

		// Returns boolean indicating if color should be inverted
		return this.invertcolors;

	} // End of Method declaration

	/***
	 * Getter method for the Returned Color Space
	 * @return A string value identifying the returned color space.
	 */
	public String getRetSpace() {

		// The string with the color space to use for returned values
		return this.retspace;

	} // End of Method declaration

	/***
	 * Getter method for the Input Color Space
	 * @return A string value identifying the input color space.
	 */
	public String getInSpace() {

		// The string with the color space to use for input values
		return this.inspace;

	} // End of Method declaration

	/***
	 * Method to return the string array of colors to Stata
	 * @param colors A string array of RGB values to be passed back to Stata
	 */
	public void toStata(String[] colors) {
		
		// Loop over the elements of the string array
		for (int i = 0; i < colors.length; i++) {

			// Create a name for the macro
			String name = "color" + (i + 1);
			
			// Set a macro with the name defined above with the value for the 
			// corresponding string array element
			Macro.setLocal(name, colors[i]);

		} // End Loop over string array elements
		
	} // End of toStata method declaration

} // End of Class declaration
