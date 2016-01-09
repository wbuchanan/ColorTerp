package org.paces.stata;
import com.stata.sfi.Macro;
import javafx.scene.paint.Color;

/***
 * @author Billy Buchanan
 * @version 0.0.1-SNAPSHOT
 *
 * Class and methods used for color interpolation in Stata.
 * To make testing a bit easier, the class includes a main method that
 * provides a command line interface and prints the results to the console.
 * The arguments for the main method are described in further detail at
 * {@link org.paces.stata.ColorTerp#main(String[])}.
 * However, the primary method to interact with the class and methods should
 * occur through the brewterpolate.ado Stata command, which acts
 * as a wrapper around this class by accessing it through the Stata Java API.
 *
 * You can find additional information out about the Java plugin described
 * here at the
 * <a href="https://github.com/wbuchanan/ColorTerp">project repository</a>,
 * or by visiting the
 * <a href="https://wbuchanan.github.io/brewscheme/">brewscheme project page</a> or
 * <a href="https://github.com/wbuchanan/brewscheme">brewscheme repository.</a>
 *
 * <h2>Examples</h2>
 * // Interpolate 4 colors in RGB colorspace <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(4) <br><br>
 *
 * // Interpolate 9 colors in the same color space and return the inverse of the colors <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(9) inv <br><br>
 *
 * // Interpolate 3 colors in RGB and return the colors as grayscale <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(3) g <br><br>
 *
 * // 5 Saturated colors as Web formatted RGB Hexadecimal strings <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(5) rcs(web) cm(saturated) <br><br>
 *
 * // 5 Less saturated colors as Hexadecimal values with alpha parameter <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(5) rcs(hexa) cm(desaturated) <br><br>
 *
 * // The inverse of 37 Colors that are arbitrarily brighter in HSB colorspace <br>
 * brewterpolate, sc("197 115 47") ec("5, 37, 249") c(37) inv cm(brighter) rcs(hsb) <br><br>
 *
 *
 * @see <a href="http://www.stata.com/java/api">Stata Java API.</a>
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

	/**
	 * Member used to define whether or not to return the values of the
	 * interpolated colors in a grayscale space.
	 */
	private boolean grayscale;


	/**
	 * Method used to set the value of the grayscale member
	 * @param graysc A boolean string literal indicating if the returned
	 *                  colors are to be transformed/projected into a
	 *                  grayscale color space within the user requested color
	 *                  space (e.g., grayscale colors within HSB/Hex/RGB
	 *                  color spaces).
	 */
	public void setGrayScale(String graysc) {
		this.grayscale = Boolean.valueOf(graysc);
	}

	/***
	 * Method used to construct a color object given an array of integer
	 * values and a user specified color space
	 * @param colors An array of integer color values
	 * @param inspace RGB or RGBA only
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(int[] colors, String inspace) {

		// If user specified RGB color space
		if (colors.length == 3 && inspace.equals("rgb")) {

			// Populate object with Color defined by the rgb elements of the
			// int array
			return Color.rgb(colors[0], colors[1], colors[2]);

		// If there are four int[] elements and colorspace is rgba
		} else if (colors.length == 4 & inspace.equals("rgba")) {

			// Set rgb from first three elements of array and alpha parameter
			// from the fourth element of the array
			return Color.rgb(colors[0], colors[1], colors[2], colors[3]);

		// Should not be reached
		} else {

			// Default value to prevent compiler from crying
			return Color.STEELBLUE;

		} // End IF/ELSE Block for color handling

	} // End of Method declaration

	/***
	 * Method used to construct a color object given an array of double
	 * values and a user specified color space
	 * @param colors An array of double valued color components
	 * @param inspace sRGB, sRGBa, HSB, or HSBa
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(double[] colors, String inspace) {

		// If user specified sRGB color space and the double[] has 3 elements
		if (colors.length == 3 && inspace.equals("srgb")) {

			// Use the 3 array elements to define the rgb properties in sRGB
			return Color.color(colors[0], colors[1], colors[2]);

		// If user specified sRGBa color space and the double[] has 4 elements
		} else if (colors.length == 4 & inspace.equals("srgba")) {

			// First 3 elements are rgb, 4th is alpha
			return Color.color(colors[0], colors[1], colors[2], colors[3]);

		// If user specified sRGB color space and the double[] has 3 elements
		} else if (colors.length == 3 && inspace.equals("hsb")) {

			// Use the 3 array elements to define the hsb properties in HSB
			return Color.hsb(colors[0], colors[1], colors[2]);

		// If user specified sRGB color space and the double[] has 3 elements
		} else if (colors.length == 4 && inspace.equals("hsba")) {

			// First 3 elements are hsb, 4th is alpha
			return Color.hsb(colors[0], colors[1], colors[2], colors[3]);

		// Should not be reached
		} else {

			// Default value to prevent compiler from crying
			return Color.STEELBLUE;

		} // End IF/ELSE Block for color handling

	} // End of Method declaration


	/***
	 * Method used to construct a color object given an array of integer
	 * values and a user specified color space
	 * @param colors An array of String with one or two elements
	 * @param inspace web only
	 * @return An object of class Color given the RGB input
	 */
	public Color setColor(String[] colors, String inspace) {

		// If hex with alpha in single hex string
		if (colors.length == 1 &&
				("weba".equals(inspace) || "hexa".equals(inspace))) {

			// First six bytes are the RGB values
			String hex = colors[0].substring(0, 5);

			// Last two bytes should be the alpha scaled to be in [0, 255]
			Double alpha = Integer.decode(colors[0].substring(6, 7)) / 255.0;

			// Second argument would be for alpha transparency
			return Color.web(hex, alpha);

		// If the string array contains multiple elements
		} else if (colors.length > 1) {

			// Other option is to pass alpha as decimal value with space
			// delimiter between it and the hex string
			return Color.web(colors[0], Double.valueOf(colors[1]));

		// In all other cases default to single parameter method invocation
		} else {

			// Treat the single string as a Hexadecimal RGB value
			return Color.web(colors[0]);

		} // End ELSE Block

	} // End of Method declaration

	/***
	 * Setter method for the input color space
	 * @param icspace The input color space used for the scolor and ecolor
	 *                   parameters in brewterpolate.ado
	 */
	public void setInSpace(String icspace) {

		// Sets the input color space variable
		this.inspace = icspace;

	} // End of setter for input color space

	/***
	 * Setter method for the returned color space
	 * @param rcspace A string value identifying the colorspace in which the
	 *                   results are to be returned
	 */
	public void setRetSpace(String rcspace) {

		// Sets the return value color space
		this.retspace = rcspace;

	} // End of setter for return value color space

	/***
	 * Setter method for inverted colors
	 * @param inverse A boolean string literal indicating if the inverse of
	 *                   the interpolated colors are to be returned.
	 */
	public void setInverse(String inverse) {

		// Set inverted colors to boolean version of string
		this.invertcolors = Boolean.valueOf(inverse);

	} // End of setter for inverse colors

	/***
	 * Setter method for brightness, darkness, saturated, and/or desaturated
	 * variables
	 * @param b String argument from the color modification parameter in the
	 *             Stata ado
	 */
	public void setBrighter(String b) {

		// Switch statement for allowable values for color mod parameter from
		// the Stata brewterpolate ado wrapper.
		switch(b) {

			// For brighter set brighter true and all else false
			case "brighter":
				this.brighter = true;
				this.darker = false;
				this.saturated = false;
				this.desaturated = false;
				break;
			// For darker set darker true and all else false
			case "darker":
				this.brighter = false;
				this.darker = true;
				this.saturated = false;
				this.desaturated = false;
				break;

			// Saturated sets saturated to true
			case "saturated":
				this.brighter = false;
				this.darker = false;
				this.saturated = true;
				this.desaturated = false;
				break;

			// Desaturated sets desaturated to true
			case "desaturated":
				this.brighter = false;
				this.darker = false;
				this.saturated = false;
				this.desaturated = true;
				break;

			// For every other case set everything to false
			default:
				this.brighter = false;
				this.darker = false;
				this.saturated = false;
				this.desaturated = false;
				break;

		} // End of Switch statement

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

			// Set the value of the first element to 0
			tmp[0] = 0.0;

		// For all other cases
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

	/**
	 * Method used to convert an RGB Integer value to a Hexadecimal string
	 * @param val A double valued RGB component value
	 * @return A two byte hexadecimal string.  If the value is in [0, 15] the
	 * method will append the leading zero in the first position of the
	 * string (e.g., 11 in decimal becomes "0b" instead of "b").
	 */
	public String rgbToHex(Double val) {

		// Converts the value first to Integer and then to a Hexadecimal String
		// If in [0, 15] returns value with appended "0" in the first position
		return Integer.toHexString(rgbInt(val)).length() == 1 ?
				"0" + Integer.toHexString(rgbInt(val)) :
				Integer.toHexString(rgbInt(val));

	} // End of method declaration

	/**
	 * Method to transform double valued RGB component value to an Integer
	 * value in [0, 255]
	 * @param val The double value to convert to an RGB integer
	 * @return An integer value in [0, 255] based on the double value passed
	 * to the method.
	 */
	public Integer rgbInt(Double val) {

		// If the rounded value is less than 255 else impose a cieling value of 255
		return Math.round(val * 255) < 255 ? (int) Math.round(val * 255) : 255;

	} // End Method declaration

	/***
	 * Method to translate color object into a color string
	 * @param thecolor The interpolated color object
	 * @param cspace The return color space
	 * @return A space delimited string of component values for the
	 * interpolated colors.
	 */
	public String getColorString(Color thecolor, String cspace) {

		// Returns string based on color space
		switch (cspace) {

			// Hexadecimal based formats
			case "web":
			case "weba": {

				// Convert the red channel value to a hexadecimal string
				String r = "#" + rgbToHex(thecolor.getRed());

				// Convert the green channel value to a hexadecimal string
				String g = rgbToHex(thecolor.getGreen());

				// Convert the blue channel value to a hexadecimal string
				String b = rgbToHex(thecolor.getBlue());

				// If web is output color space
				if ("web".equals(cspace)) return r + g + b;

				// If web with alpha transparency parameter
				else return r + g + b + " " + String.valueOf(thecolor.getOpacity());

			} // End CASE for Web-based Colors

			// Case for hexadecimal strings non-web format
			case "hex":
			case "hexa": {

				// Convert the red channel value to a hexadecimal string
				String r = rgbToHex(thecolor.getRed());

				// Convert the green channel value to a hexadecimal string
				String g = rgbToHex(thecolor.getGreen());

				// Convert the blue channel value to a hexadecimal string
				String b = rgbToHex(thecolor.getBlue());

				// For hexadecimal with alpha parameter
				if ("hexa".equals(cspace)) return r + g + b + " " + String.valueOf(thecolor.getOpacity());

				// For hexadecimal without alpha parameter
				else return r + g + b;

			} // End Hexadecimal-based return spaces

			// Decimal RGB formats
			case "srgb":
			case "srgba": {

				// Get String value of the red color rounded to nearest integer
				String r = String.valueOf(thecolor.getRed());

				// Get String value of the green color rounded to nearest integer
				String g = String.valueOf(thecolor.getGreen());

				// Get String value of the blue color rounded to nearest integer
				String b = String.valueOf(thecolor.getBlue());

				// If colorspace includes alpha layer transparency
				if (cspace.equals("srgba")) {

					// Add the space delimited string as an array element
					return r + " " + g + " " + b + " " + String.valueOf(thecolor.getOpacity());

					// For sRGB without alpha transparency
				} else {

					// Add the space delimited string as an array element
					return r + " " + g + " " + b;

				} // End ELSE Block for sRGB

			} // End CASE for decimal RGB values

			// Case for Hue Saturation Brightness colorspace
			case "hsb":
			case "hsba": {

				// Get String value of the hue
				String h = String.valueOf(thecolor.getHue());

				// Get String value of the saturation
				String s = String.valueOf(thecolor.getSaturation());

				// Get String value of the brightness
				String b = String.valueOf(thecolor.getBrightness());

				// If colorspace includes alpha layer transparency
				if (cspace.equals("hsba")) {

					// Add the space delimited string as an array element
					return h + " " + s + " " + b + " " + String.valueOf(thecolor.getOpacity());

					// For HSB without alpha transparency
				} else {

					// Add the space delimited string as an array element
					return h + " " + s + " " + b;

				} // End ELSE Block for HSB w/o alpha transparency

			} // End CASE for HSB colorspace

			// For integer valued RGB
			default: {

				// Get String value of the red color rounded to nearest integer
				String r = String.valueOf(rgbInt(thecolor.getRed()));

				// Get String value of the green color rounded to nearest integer
				String g = String.valueOf(rgbInt(thecolor.getGreen()));

				// Get String value of the blue color rounded to nearest integer
				String b = String.valueOf(rgbInt(thecolor.getBlue()));

				// For RGB with alpha transparency
				if (cspace.equals("rgba")) {

					// Add the space delimited string as an array element
					return r + " " + g + " " + b + " " + String.valueOf(thecolor.getOpacity());

					// For RGB without alpha transparency
				} else {

					// Add the space delimited string as an array element
					return r + " " + g + " " + b;

				} // End ELSE Block for RGB

			} // End CASE for integer RGB values

		} // End Switch statement

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
	 * @param grayscale A boolean used to translate the color into a
	 *                     grayscale space
	 */
	public void setTColors(Color s, Color e, double[] distances, boolean
			brighter, boolean darker, boolean saturated,
			boolean desaturated, boolean invert, String cspace,
			boolean grayscale) {

		// Sets up storage object
		String[] colors = new String[distances.length];

		// Loop over the distances
		for(int i = 0; i < distances.length; i++) {

			Color tmpColor;

			// Arbitrarily brighter colors
			if (brighter && !darker && !saturated && !desaturated) {

				// Make color arbitrarily darker
				tmpColor = s.interpolate(e, distances[i]).brighter();

			// Arbitrarily darker colors
			} else if (!brighter && darker && !saturated && !desaturated) {

				// Make color arbitrarily darker
				tmpColor = s.interpolate(e, distances[i]).darker();

			// Arbitrarily more saturated colors
			} else if (!brighter && !darker && saturated && !desaturated) {

				// Make color arbitrarily saturated
				tmpColor = s.interpolate(e, distances[i]).saturate();

			// Arbitrarily less saturated colors
			} else if (!brighter && !darker && !saturated && desaturated) {

				// Make color arbitrarily desaturated
				tmpColor = s.interpolate(e, distances[i]).desaturate();

			// Unmodified colors
			} else {

				// Color without brightness/saturation modified
				tmpColor = s.interpolate(e, distances[i]);

			} // End ELSE Block for unmodified colors

			// Check for inverted color boolean
			if (invert) tmpColor = tmpColor.invert();

			// Get the inverse of the current color
			if (grayscale) colors[i] = getColorString(tmpColor.invert(), cspace);

			// Store the color string in the ith array element
			else colors[i] = getColorString(tmpColor, cspace);

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
		this.points = Integer.valueOf(ptmacro) + 1;

	} // End of Method declaration


	/***
	 * Generic constructor method
	 * @param args Array of string arguments...not used.
	 */
	public ColorTerp(String[] args) {

		// Set the input color space
		setInSpace(args[0]);

		// Set the return color space
		setRetSpace(args[1]);

		// Starting Color
		setStart(args[2], getInSpace());
		
		// Ending Color
		setEnd(args[3], getInSpace());

		// Number of colors to interpret between start and end
		setPoints(args[4]);
		
		// Set an array of doubles containing the distances between start and 
		// end colors
		setDistances(getPoints());

		// Passes argument to set one of the options from the color
		// modification parameter in the brewterpolate ado wrapper
		setBrighter(args[5]);

		// Passes argument to set the inverted colors parameter
		setInverse(args[6]);

		// Passes argument to set the grayscale parameter
		setGrayScale(args[7]);
		
		// Get interpolated colors
		setTColors(getStart(), getEnd(), getDists(), getBrighter(),
				getDarker(), getInvertColors(), getSaturated(), getDesaturated(),
				getRetSpace(), getGrayScale());
		
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

	/**
	 * Command line interface method
	 * @param args A string array containing :
	 *             <ol>
	 *             		<li>Input Color Space String</li>
	 *             		<li>Output Color Space String</li>
	 *             		<li>Starting Color String</li>
	 *             		<li>Ending Color String</li>
	 *             		<li>Number of points to interpolate</li>
	 *             		<li>Color mod argument (one of brighter, darker,
	 *             		saturated, desaturated, or "") </li>
	 *             		<li>Invert color (boolean string)</li>
	 *             		<li>Return colors as grayscale</li>
	 *             </ol>
	 */
	public static void main(String[] args) {

		// Initialize new ColorTerp object
		ColorTerp theColors = new ColorTerp(args);

		// Get the interpolated colors
		String[] interpedColors = theColors.getTColors();

		// Print the starting color to the console
		System.out.println(theColors.getColorString(theColors.getStart(),
				theColors.getRetSpace()));

		// Loop over other colors
		for (String interpedColor : interpedColors) {

			// Print the color to the console
			System.out.println(interpedColor);

		} // End Loop over other colors

	} // End main method declaration

	/**
	 * Method to return the boolean value to return the colors as gray scale
	 * @return A boolean indicating if gray scale colors were requested by
	 * the user
	 */
	public boolean getGrayScale() {
		return this.grayscale;
	}

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

		// Returns the starting color in the first position
		Macro.setLocal("color1", getColorString(getStart(), getRetSpace()));

		// Loop over the elements of the string array
		for (int i = 0; i < colors.length; i++) {

			// Create a name for the macro
			String name = "color" + (i + 2);

			// Set a macro with the name defined above with the value for the
			// corresponding string array element
			Macro.setLocal(name, colors[i]);

		} // End Loop over string array elements

	} // End of toStata method declaration

} // End of Class declaration
