import ij.gui.GenericDialog;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Color_Temperature_v1
 * @author YiQi Nien
 * Created Jan 3, 2015
 * Finished on Jan 7 2015.
 */

/**
 * Color_Temperature_v2
 * @author YiChi Nien
 * Update since Jan 10 2015,
 * 
 * Memo: (1)Insert a dialog which can select compute Color Temperature and compute Object Spectral DIsplay.
 * 		 (2)Read two different format of the illuminance R.
 * 		 (3)Show the Chromaticity Diagram of CCT.
 * 		 (4)Demo the CRI.
 * 
 * 		(2)Finished on Jan 10 2015.
 * 		(1)Finished on Jan 12 2015.
 * 		(3)Finished on oo.
 * 		(4)
 * 		All finished on
 */

public class Color_Temperature_v3{	
	static double DBL_MIN = 0;
	
	static double[] rt = {       /* reciprocal temperature (K) */
			DBL_MIN,0.00001,0.00002,0.00003,0.00004,0.00005,0.00006,0.00007,0.00008,0.00009,0.0001	
			,0.000125,0.00015,0.000175,0.0002,0.000225,0.00025,0.000275,0.0003
			,0.000325,0.00035,0.000375,0.0004,0.000425,0.00045,0.000475,0.0005
			,0.000525,0.00055,0.000575,0.0006};
    
	static double [][] uvt = {
	        {0.18006, 0.26352, -0.24341},
	        {0.18066, 0.26589, -0.25479},
	        {0.18133, 0.26846, -0.26876},
	        {0.18208, 0.27119, -0.28539},
	        {0.18293, 0.27407, -0.30470},
	        {0.18388, 0.27709, -0.32675},
	        {0.18494, 0.28021, -0.35156},
	        {0.18611, 0.28342, -0.37915},
	        {0.18740, 0.28668, -0.40955},
	        {0.18880, 0.28997, -0.44278},
	        {0.19032, 0.29326, -0.47888},
	        {0.19462, 0.30141, -0.58204},
	        {0.19962, 0.30921, -0.70471},
	        {0.20525, 0.31647, -0.84901},
	        {0.21142, 0.32312, -1.0182},
	        {0.21807, 0.32909, -1.2168},
	        {0.22511, 0.33439, -1.4512},
	        {0.23247, 0.33904, -1.7298},
	        {0.24010, 0.34308, -2.0637},
	        {0.24792, 0.34655, -2.4681},	/* Note: 0.24792 is a corrected value for the error found in W&S as 0.24702 */
	        {0.25591, 0.34951, -2.9641},
	        {0.26400, 0.35200, -3.5814},
	        {0.27218, 0.35407, -4.3633},
	        {0.28039, 0.35577, -5.3762},
	        {0.28863, 0.35714, -6.7262},
	        {0.29685, 0.35823, -8.5955},
	        {0.30505, 0.35907, -11.324},
	        {0.31320, 0.35968, -15.628},
	        {0.32129, 0.36011, -23.325},
	        {0.32931, 0.36038, -40.770},
	        {0.33724, 0.36051, -116.45}
	};
	
	public static class ColorSpaceConverter {
	    /**
	     * reference white in XYZ coordinates
	     */
	    public double[] xyz_D50 = {96.4212, 100.0, 82.5188};
	    public double[] xyz_D55 = {95.6797, 100.0, 92.1481};
	    public double[] xyz_D65 = {95.0429, 100.0, 108.8900};
	    public double[] xyz_D75 = {94.9722, 100.0, 122.6394};
	    public double[] whitePoint = xyz_D65;

	    /**
	     * reference white in xyY coordinates
	     */
	    public double[] chromaD50 = {0.3457, 0.3585, 100.0};
	    public double[] chromaD55 = {0.3324, 0.3474, 100.0};
	    public double[] chromaD65 = {0.3127, 0.3290, 100.0};
	    public double[] chromaD75 = {0.2990, 0.3149, 100.0};
	    public double[] chromaWhitePoint = chromaD65;
	    /**
	     * XYZ to sRGB conversion matrix for D65
	     */
	    public double[][] MsRGB65  = {{ 3.2406, -1.5372, -0.4986},
	                             	  {-0.9689,  1.8758,  0.0415},
	                             	  { 0.0557, -0.2040,  1.0570}};
	    
	    /**
	     * XYZ to sRGB conversion matrix for D50
	     */
	    public double[][] MsRGB50  = {{ 3.1338561, -1.6168667, -0.4906146},
	                             	  {-0.9787684,  1.9161415,  0.0334540},
	                             	  { 0.0719453, -0.2289914,  1.4052427}};
	    
	    /**
	     * XYZ to Adobe RGB conversion matrix for D65
	     */
	    public double[][] MAdobe65  = {{ 2.0413690, -0.5649464, -0.3446944},
	                             	   {-0.9692660,  1.8760108,  0.0415560},
	                             	   { 0.0134474, -0.1183897,  1.0154096}};
	    
	    /**
	     * XYZ to Adobe RGB conversion matrix for D50
	     */
	    public double[][] MAdobe50  = {{ 1.9624274, -0.6105343, -0.3413404},
	                             	   {-0.9787684,  1.9161415,  0.0334540},
	                             	   { 0.0286869, -0.1406752,  1.3487655}};
	         
	    /**
	     * XYZ to Apple RGB conversion matrix for D65
	     */
	    public double[][] MApple65  = {{ 2.9515373, -1.2894116, -0.4738445},
	                             	   {-1.0851093,  1.9908566,  0.0372026},
	                             	   { 0.0854934, -0.2694964,  1.0912975}};
	    
	    /**
	     * XYZ to Apple RGB conversion matrix for D50
	     */
	    public double[][] MApple50  = {{ 2.8510695, -1.3605261, -0.4708281},
	                             	   {-1.0927680,  2.0348871,  0.0227598},
	                             	   { 0.1027403, -0.2964984,  1.4510659}};
	    
	    /**
	     * XYZ to Bruce RGB conversion matrix for D65
	     */
	    public double[][] MBruce65  = {{ 2.7454669, -1.1358136, -0.4350269},
	                             	   {-0.9692660,  1.8760108,  0.0415560},
	                             	   { 0.0112723, -0.1139754,  1.0132541}};
	    
	    /**
	     * XYZ to Bruce RGB conversion matrix for D50
	     */
	    public double[][] MBruce50  = {{ 2.6502856, -1.2014485, -0.4289936},
	                             	   {-0.9787684,  1.9161415,  0.0334540},
	                             	   { 0.0264570, -0.1361227,  1.3458542}};
	    
	    /**
	     * default constructor, uses D65 for the white point
	     */
	    public ColorSpaceConverter() {
	      whitePoint = xyz_D65;
	      chromaWhitePoint = chromaD65;
	    }

	    /**
	     * constructor for setting a non-default white point
	     * @param white String specifying the white point to use
	     */
	    public ColorSpaceConverter(String white) {
	      whitePoint = xyz_D65;
	      chromaWhitePoint = chromaD65;
	      if (white.equalsIgnoreCase("d50")) {
	        whitePoint = xyz_D50;
	        chromaWhitePoint = chromaD50;
	      }
	      else if (white.equalsIgnoreCase("d55")) {
	        whitePoint = xyz_D55;
	        chromaWhitePoint = chromaD55;
	      }
	      else if (white.equalsIgnoreCase("d65")) {
	        whitePoint = xyz_D65;
	        chromaWhitePoint = chromaD65;
	      }
	      else if (white.equalsIgnoreCase("d75")) {
	        whitePoint = xyz_D75;
	        chromaWhitePoint = chromaD75;
	      }
	    }
	    /**
	     * Convert XYZ to RGB
	     * @param XYZ in a double array.
	     * @return RGB in int array.
	     */
	    public int[] XYZto65sRGB3(double X, double Y, double Z) {
	      int[] result = new int[3];

	      double x = X / 100.0;
	      double y = Y / 100.0;
	      double z = Z / 100.0;

	      // [r g b] = [X Y Z][M]
	      double r = (x * MsRGB65[0][0]) + (y * MsRGB65[0][1]) + (z * MsRGB65[0][2]);
	      double g = (x * MsRGB65[1][0]) + (y * MsRGB65[1][1]) + (z * MsRGB65[1][2]);
	      double b = (x * MsRGB65[2][0]) + (y * MsRGB65[2][1]) + (z * MsRGB65[2][2]);

	      // assume sRGB
	      if (r > 0.0031308) {
	        r = ((1.055 * Math.pow(r, 1.0 / 2.4)) - 0.055);
	      }
	      else {
	        r = (r * 12.92);
	      }
	      if (g > 0.0031308) {
	        g = ((1.055 * Math.pow(g, 1.0 / 2.4)) - 0.055);
	      }
	      else {
	        g = (g * 12.92);
	      }
	      if (b > 0.0031308) {
	        b = ((1.055 * Math.pow(b, 1.0 / 2.4)) - 0.055);
	      }
	      else {
	        b = (b * 12.92);
	      }

	      r = (r < 0) ? 0 : r;
	      g = (g < 0) ? 0 : g;
	      b = (b < 0) ? 0 : b;

	      // convert 0..1 into 0..255
	      result[0] = (int) Math.round(r * 255);
	      result[1] = (int) Math.round(g * 255);
	      result[2] = (int) Math.round(b * 255);

	      return result;
	    }  
	    /**
	     * Convert XYZ to RGB
	     * @param XYZ in a double array.
	     * @return RGB in int array.
	     */
	    public int[] XYZto65sRGB1(double[] XYZ) {
	      return XYZto65sRGB3(XYZ[0], XYZ[1], XYZ[2]);
	    }
	    /**
	     * Convert XYZ to Adobe RGB
	     * @param XYZ in a double array.
	     * @return Adobe RGB in int array.
	     */
	    public int[] XYZto65AdobeRGB3(double X, double Y, double Z) {
	        int[] result = new int[3];

	        double x = X / 100.0;
	        double y = Y / 100.0;
	        double z = Z / 100.0;

	        // [r g b] = [X Y Z][M]
	        double r = (x * MAdobe65[0][0]) + (y * MAdobe65[0][1]) + (z * MAdobe65[0][2]);
	        double g = (x * MAdobe65[1][0]) + (y * MAdobe65[1][1]) + (z * MAdobe65[1][2]);
	        double b = (x * MAdobe65[2][0]) + (y * MAdobe65[2][1]) + (z * MAdobe65[2][2]);

	        // assume sRGB
	        if (r > 0.0031308) {
	          r = ((1.055 * Math.pow(r, 1.0 / 2.4)) - 0.055);
	        }
	        else {
	          r = (r * 12.92);
	        }
	        if (g > 0.0031308) {
	          g = ((1.055 * Math.pow(g, 1.0 / 2.4)) - 0.055);
	        }
	        else {
	          g = (g * 12.92);
	        }
	        if (b > 0.0031308) {
	          b = ((1.055 * Math.pow(b, 1.0 / 2.4)) - 0.055);
	        }
	        else {
	          b = (b * 12.92);
	        }

	        r = (r < 0) ? 0 : r;
	        g = (g < 0) ? 0 : g;
	        b = (b < 0) ? 0 : b;

	        // convert 0..1 into 0..255
	        result[0] = (int) Math.round(r * 255);
	        result[1] = (int) Math.round(g * 255);
	        result[2] = (int) Math.round(b * 255);

	        return result;
	      }  
	      /**
	       * Convert XYZ to Adobe RGB
	       * @param XYZ in a double array.
	       * @return Adobe RGB in int array.
	       */
	      public int[] XYZto65AdobeRGB1(double[] XYZ) {
	        return XYZto65AdobeRGB3(XYZ[0], XYZ[1], XYZ[2]);
	      }
	  }
	
	static String illuminant_path;
	static String CIE_path;
	static String illuminant_Name;
	static String CIE_Name;
	static String ObjectSR_path;
	static BufferedReader linesreader;
	
	static String[] message = {"Please choose your data of illuminant source.",
								"And choose the data of CIE color matching functions.",
								"Then choose your data of Object CMYK spectral range."};
	static int i;
	static int j;
	static int l;
	static double k;		//K value
	
	static int cc=0;		//choice count
	static int illuminant = 0, ColorMatchingFunction = 1,CMYKspectra = 2;
	
	static int SR_Range = 0;
	static int CMYK = 4;
	static int Chroma_Range = 20;
	static String service;
	static String[] Service = {"Color Temperature Calculation", "Object Spectral Reflectance Measure and Show in CMYK"};
	
	static String space;
	String concentration_range;
	static String whitePoint;
	static ColorSpaceConverter csc;
	static String[] Spaces = {"sRGB", "AdobeRGB"};
	
	static boolean showMainDialog() {
	    // create a dialog
	    GenericDialog dialog = new GenericDialog("Color Management");
	    dialog.addChoice("Function: ", Service, service);
	    
	    // show the dialog, quit if the user clicks "cancel"
	    dialog.showDialog();
	    if (dialog.wasCanceled()) { return false; }

	    // set default choices
	 	service = "Color Temperature Calculation";
	 	
	    // get options
	 	service = dialog.getNextChoice();
	    
	    return true;
	}
	
	static boolean showObjectMenuDialog() {
	    // create a dialog
		GenericDialog dialog = new GenericDialog("Choose Color Space");	    
	    dialog.addChoice("Color Space :", Spaces, space);

	    // show the dialog, quit if the user clicks "cancel"
	    dialog.showDialog();
	    if (dialog.wasCanceled()) { return false; }
	    
	    // set default choices
	 	space = "sRGB";
	 	whitePoint = "D65";

	    // get options
	    space = dialog.getNextChoice();

		csc = new ColorSpaceConverter(whitePoint);
	    
	    return true;
	}
	
	public static void main (String args[]){ 		
		while(true){
			if (!showMainDialog())
				return;
			
			switch(service){
		      case "Color Temperature Calculation": {	    	  
		    	  cc = illuminant;
		    	  ChooseFile();
		    	  cc = ColorMatchingFunction;
		    	  ChooseFile();
		    	  
		    	  double[] xyzofWhitePoint = XYZforWhitePoint();
		    	  
		    	  XYZtoCorColorTemperature(xyzofWhitePoint);	    	  
		      }
		      break;
		      case "Object Spectral Reflectance Measure and Show in CMYK": {
		    	  if(!showObjectMenuDialog())
		    		  return;
		    	  cc = illuminant;
		    	  ChooseFile();
		    	  cc = ColorMatchingFunction;
		    	  ChooseFile();
		    	  
		    	  cc = CMYKspectra;
		    	  ChooseFile();
		    	  Object_CMYK_Spectral_Display();
		      }
		      break;
			}
		}
		
	}
	
	public static void ChooseFile()
	  {
	  	JOptionPane.showMessageDialog(null, message[cc]);
	  	JFileChooser fileChooser = new JFileChooser();
	      fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );

	      int result = fileChooser.showOpenDialog( null );

	      if ( result == JFileChooser.CANCEL_OPTION )
	         System.exit( 1 );

	      File fileName = fileChooser.getSelectedFile();

	      if ( ( fileName == null ) || ( fileName.getName().equals( "" ) ) )
	      {
	         JOptionPane.showMessageDialog( null, "Invalid Name", "Invalid Name", JOptionPane.ERROR_MESSAGE );
	         System.exit( 1 );
	      }
	      System.out.printf("%s\n",fileName.getName());
	      
	      if(cc == illuminant){
	    	  illuminant_path = fileName.getPath();
	    	  illuminant_Name = fileName.getName();
	      }
	      if(cc == ColorMatchingFunction){
	    	  CIE_path = fileName.getPath();
	    	  CIE_Name = fileName.getName();
	      }
	      if(cc == CMYKspectra){
	    	  ObjectSR_path = fileName.getPath();
	      }
	  }
	
	public static ArrayList<Double> illuminantSR(){		
		ArrayList SRlist = new ArrayList();
		  ArrayList<Double> SRvalue = new ArrayList<Double>();
		  ArrayList<Double> SRvalue2 = new ArrayList<Double>();
		  String line;
		  String tempString;
		  String checkString;
		  String[] checkarray;
		  String[] tempArray;
		  String[] tempArray2;
		  int R = 380;
		  	 
		  try{		  
	    	  System.out.printf("\n%s\n", illuminant_path);
			  linesreader = new BufferedReader(new FileReader(illuminant_path));	
			  
			  while((line=linesreader.readLine())!=null){
				  checkString = line;
				  checkarray=checkString.split("\">");			
				  
				  tempArray = checkarray;

				  for(i=1;i<tempArray.length;i++){
					  tempArray2=tempArray[i].split("<");
					  SRlist.add(tempArray2[i-1]);	
				  }

				  checkString = line;
				  checkarray=checkString.split("0\t");
				  
				  tempArray=checkarray;	
				  for(i=1;i<tempArray.length;i++)
					  SRlist.add(tempArray[i]);	
			  }		  
			  linesreader.close();
	
			  SR_Range = SRlist.size();
			  System.out.printf("\n<The original data of illuminance R>");
			  for(i=0;i<SR_Range;i++){
				  SRvalue.add(Double.parseDouble((String)SRlist.get(i)));
				  System.out.printf("\n"+R+"\t%f", SRvalue.get(i));
				  R += 10;
			  }
			  System.out.printf("\n");
			  
			  R=380;		  
			  if(SRvalue.get(18)!=100) {		//560nm value normalize 100
				  System.out.printf("\n<560nm value normalize 100>");
				  for(i=0;i<SR_Range;i++){
					  SRvalue2.add(SRvalue.get(i)/SRvalue.get(18)*100);
					  System.out.printf("\n"+R+"\t%f", SRvalue2.get(i));
					  R +=10;
				  }
				  System.out.printf("\n\n");
			  }
			  else {
				  System.out.printf("\n<560nm value normalize 100>");
				  for(i=0;i<SR_Range;i++){
					  SRvalue2.add(SRvalue.get(i));
					  System.out.printf("\n"+R+"\t%f", SRvalue2.get(i));
					  R +=10;
				  }
				  System.out.printf("\n\n");
			  }
		  }
		  catch(Exception e){
		  }
		  return SRvalue2;
	}
  
	public static double[][] CIExyz(){		
		ArrayList CIElist = new ArrayList();
		ArrayList<Double> CIEvalue = new ArrayList<Double>();
		String line;
		String tempString;		
		String[] tempArray;
		double[][] CIExyz = null;
		int R = 380;
		
		  try{	  
			  System.out.printf("%s\n", CIE_path);
			  linesreader = new BufferedReader(new FileReader(CIE_path));
			  while((line=linesreader.readLine())!=null){
				  tempString = line;
				  tempArray=tempString.split("\t");	
				  for(i=1;i<tempArray.length;i++){
					  CIElist.add(tempArray[i]);
				  }
			  }
			  linesreader.close();
	
			  CIExyz = new double[SR_Range][3];
			  for(int k=0;k<CIElist.size();k++)
				  CIEvalue.add(Double.parseDouble((String)CIElist.get(k)));			  
			  for(i=0;i<SR_Range;i++){
				  for(j=0;j<3;j++)
					  CIExyz[i][j] =CIEvalue.get(3*i+j);
			  }	
			  System.out.printf("\n<The CIE matching function data>\n");
			  for(i=0;i<SR_Range;i++){
				  System.out.printf("%d",R);
				  for(j=0;j<3;j++){
					  System.out.printf("\t%f", CIExyz[i][j]);
				  }
				  System.out.printf("\n");
				  R +=10;
			  }	
		  }
		    catch (Exception e)
		    {
		    }
		  return CIExyz;
	}
	
	public static double[] XYZforWhitePoint(){
		ArrayList<Double> SR = illuminantSR();
		double[][] CIExyz = CIExyz();
 	  	 
  	  	double[][] xyzbar = new double[SR_Range][3];
  	  	double[] xyz = new double[3];
  	  	double[] xyzWhitePoint = new double[3];
  	  	double[] tempxyz = new double[3];
  	  	double[][] CDxyz = new double[SR_Range][3];	//CD means Chromaticity Diagram
  	  	
  	  	for(i=0;i<SR_Range;i++){
  	  		for(j=0;j<3;j++){
  	  			xyzbar[i][j] = SR.get(i)*CIExyz[i][j]*10;
  			}
  	  		
  	  		xyz[0] += xyzbar[i][0];
			xyz[1] += xyzbar[i][1];
			xyz[2] += xyzbar[i][2];
  	  	}
  	  	
	  	//Scale Color Temperature.
  		
  		k = 100/xyz[1];
  		
  		//Y value
  		xyzWhitePoint[0] = xyz[0]*k;
  		xyzWhitePoint[1] = xyz[1]*k;
  		xyzWhitePoint[2] = xyz[2]*k;
  		
  		System.out.printf("\n%f\t%f\t%f\n",xyzWhitePoint[0],xyzWhitePoint[1],xyzWhitePoint[2]);
  		return xyzWhitePoint;
	}
	
	public static int XYZtoCorColorTemperature(double[] CCTxyz){
          double us, vs;		
          double p, di=0, dm;	//p = parameter(interpolation)
          int cl;				//CCT loop
          int Tc;
          
          if ((CCTxyz[0] < 0.01) && (CCTxyz[1] < 1.0e-20) && (CCTxyz[2] < 1.0e-20))
                  return(-1);     // protect against possible divide-by-zero failure 
          us = (4.0 * CCTxyz[0]) / (CCTxyz[0] + 15.0 * CCTxyz[1] + 3.0 * CCTxyz[2]);
          vs = (6.0 * CCTxyz[1]) / (CCTxyz[0] + 15.0 * CCTxyz[1] + 3.0 * CCTxyz[2]);
          
          dm = 0.0;
          for (cl = 0; cl < 31; cl++) {
        	  di = (vs - uvt[cl][1]) - uvt[cl][2] * (us - uvt[cl][0]);
              if ((cl > 0) && (((di < 0.0) && (dm >= 0.0)) || ((di >= 0.0) && (dm < 0.0))))
            	  break;  // found lines bounding (us, vs) : i-1 and i 
              dm = di;
          }
          
          if (cl == 31)
                  return(-2);    // bad XYZ input, color temp would be less than minimum of 1666.7 degrees, or too far towards blue 
          
          di = di / Math.sqrt(1.0 + uvt[cl][2] * uvt[cl][2]);
          dm = dm / Math.sqrt(1.0 + uvt[cl-1][2] * uvt[cl-1][2]);
          
          p = dm / (dm - di);     // p = interpolation parameter, 0.0 : i-1, 1.0 : i 
          
          //#define LERP(a,b,c)     (((b) - (a)) * (c) + (a))
          //p = 1.0 / (LERP(rt[i - 1], rt[i], p));
          p = 1.0 / ((rt[cl] - rt[cl-1])* p + rt[cl-1]);
          
          Tc = (int)p;
          System.out.printf("\nThe Color Temperature is %d.",Tc);
          
          JOptionPane.showMessageDialog(null,"Color Temperature: "+Tc ,"Measurement Result", JOptionPane.INFORMATION_MESSAGE);          
          return Tc;      // success 
	}
	
	public static ArrayList<Double> ObjectSR(){
		  ArrayList list = new ArrayList();
		  ArrayList<Double> ObSRlist = new ArrayList<Double>();
		  String line;
		  String tempString;
		  String[] tempArray;
		  int R = 380;
		  
		  try{	  
			  System.out.printf("\n%s\n\n", ObjectSR_path);
			  linesreader = new BufferedReader(new FileReader(ObjectSR_path));
			  list = new ArrayList();
			  while((line=linesreader.readLine())!=null){
				  tempString = line;
				  tempArray=tempString.split("\t");
				  for(i=1;i<tempArray.length;i++){
					  list.add(tempArray[i]);
				  }
			  }
			  linesreader.close();
			  
			  for(int k=0;k<list.size();k++)
				  ObSRlist.add(Double.parseDouble((String)list.get(k)));

			  int a=0;
			  String[] cmyk = {"C","M","Y","K"};
			  for(int f=0;f<CMYK;f++){
				  System.out.printf("<%s>\n",cmyk[f]);
				  for(int i=0;i<SR_Range;i++){
					  System.out.printf("%d",R);
					  for(int j=0;j<Chroma_Range;j++){
						  System.out.printf(" %s", list.get(a));
						  a++;
					  }
					  R +=10;
					  System.out.printf("\n");
				  }
				  R=380;
				  System.out.printf("\n");
			  }
			  
			  System.out.printf("\n");
		  }		    
		    catch (Exception e)
		    {
		    }	  
		  return ObSRlist;
	  }  
	
	public static void Object_CMYK_Spectral_Display(){
		ArrayList<Double> SR = illuminantSR();
		double[][] CIExyz = CIExyz();
		
		double[][] Cvalue;	//36, 20
		double[][] Mvalue;	//36, 20
		double[][] Yvalue;	//36, 20
		double[][] Kvalue;	//36, 20 	    
		
		Cvalue = new double[SR_Range][Chroma_Range];
		Mvalue = new double[SR_Range][Chroma_Range];
		Yvalue = new double[SR_Range][Chroma_Range];
		Kvalue = new double[SR_Range][Chroma_Range];
		
		ArrayList<Double> ObSR = ObjectSR();
		
		for(i=0;i<SR_Range;i++){
			for(j=0;j<Chroma_Range;j++){
				Cvalue[i][j] =ObSR.get(4*i*Chroma_Range+j);
			}
		}
		for(i=0;i<SR_Range;i++){
			for(j=0;j<Chroma_Range;j++){
				Mvalue[i][j] =ObSR.get(20+4*i*Chroma_Range+j);
			}
		}
		for(i=0;i<SR_Range;i++){
			for(j=0;j<Chroma_Range;j++){
				Yvalue[i][j] =ObSR.get(40+4*i*Chroma_Range+j);
			}
		}
		for(i=0;i<SR_Range;i++){
			for(j=0;j<Chroma_Range;j++){
				Kvalue[i][j] =ObSR.get(60+4*i*Chroma_Range+j);
			}
		}
		double[][] CMYKXYZ = new double [CMYK][3];
		double[][] CMYKxyz = new double [CMYK][3];
		double[][] Ccolor = new double [Chroma_Range][3];
		double[][] Mcolor = new double [Chroma_Range][3];
		double[][] Ycolor = new double [Chroma_Range][3];
		double[][] Kcolor = new double [Chroma_Range][3];
		double  CMYKk = 0;
		double[] convertTemp = new double[3];
		int w = Chroma_Range*60, h = CMYK*60;		//1200 240		
		Color[][] color = new Color[CMYK][Chroma_Range];
		
		JFrame frame = new JFrame("Object_CMYK Spectral Display for "+illuminant_Name+" & "+CIE_Name+" & "+space+" Color Space.");
		
		for(int m=0;m<CMYK;m++){
		for(j=0;j<Chroma_Range;j++){
			for(int n=0;n<3;n++){
				CMYKXYZ[m][n]=0;
				CMYKxyz[m][n]=0;
				for(i=0;i<SR_Range;i++){			//36
					CMYKXYZ[0][n] += SR.get(i)*Cvalue[i][j]*CIExyz[i][n]*10;
					CMYKXYZ[1][n] += SR.get(i)*Mvalue[i][j]*CIExyz[i][n]*10;
					CMYKXYZ[2][n] += SR.get(i)*Yvalue[i][j]*CIExyz[i][n]*10;
					CMYKXYZ[3][n] += SR.get(i)*Kvalue[i][j]*CIExyz[i][n]*10;
					CMYKxyz[m][n] += SR.get(i)*CIExyz[i][n]*10;
				}
			}	
			CMYKk = 100/CMYKxyz[m][1];
			for(int l=0;l<3;l++){
				CMYKXYZ[m][l] = CMYKXYZ[m][l]*CMYKk;
			}
			for(int o=0;o<3;o++){
				if(space=="sRGB"){				  
					convertTemp[o]=CMYKXYZ[m][o];
					CMYKXYZ[m][o] = csc.XYZto65sRGB1(convertTemp)[o];
					if(CMYKXYZ[m][o]>255)
						CMYKXYZ[m][o]=255;
					if(m==0)
						Ccolor[j][o] = CMYKXYZ[0][o];
					if(m==1)
						Mcolor[j][o] = CMYKXYZ[1][o];
					if(m==2)
						Ycolor[j][o] = CMYKXYZ[2][o];
					if(m==3)
						Kcolor[j][o] = CMYKXYZ[3][o];				  
				}		  
				else if(space=="AdobeRGB"){				  
					convertTemp[o]=CMYKXYZ[m][o];
					CMYKXYZ[m][o] = csc.XYZto65AdobeRGB1(convertTemp)[o];
					if(CMYKXYZ[m][o]>255)
						CMYKXYZ[m][o]=255;
					if(m==0)
						Ccolor[j][o] = CMYKXYZ[0][o];
					if(m==1)
						Mcolor[j][o] = CMYKXYZ[1][o];
					if(m==2)
						Ycolor[j][o] = CMYKXYZ[2][o];
					if(m==3)
						Kcolor[j][o] = CMYKXYZ[3][o];
				}
			}
		}
		} 	  
		for(int j=0;j<Chroma_Range;j++){	  
			color[0][j] = new Color((int)Ccolor[j][0],(int)Ccolor[j][1],(int)Ccolor[j][2]);
			color[1][j] = new Color((int)Mcolor[j][0],(int)Mcolor[j][1],(int)Mcolor[j][2]);
			color[2][j] = new Color((int)Ycolor[j][0],(int)Ycolor[j][1],(int)Ycolor[j][2]);
			color[3][j] = new Color((int)Kcolor[j][0],(int)Kcolor[j][1],(int)Kcolor[j][2]);	  
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(w, h);
		frame.setLayout(new GridLayout(CMYK,Chroma_Range));
		JPanel[][] panel= new JPanel[CMYK][Chroma_Range];	  
		for(int m=0;m<CMYK;m++){
			for(j=0;j<Chroma_Range;j++){
				panel[m][j]=  new JPanel();
				panel[m][j].setBackground(color[m][j]);
				frame.add(panel[m][j]);
			}	  
		}
		frame.setVisible(true);
	}
}
