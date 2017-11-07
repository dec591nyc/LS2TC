import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Color_Temperature_v1
 * @author YiQi Nien
 * Created Jan 3, 2015
 * Finished on Jan 7 2015.
 */

public class Color_Temperature_v1{	
	static String illuminant_path;
	static String CIE_path;
	static BufferedReader linesreader;
	BufferedReader datareader;
	
	static String[] message = {"Please choose your data of illuminant source.",
								"And choose the data of CIE color matching functions."};
	static int i;
	static int j;
	static int l;
	static double k;
	
	static int cc=0;
	static int SR_Range = 0;
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
		
	public static void main (String args[]){ 
		int R = 380;
		ChooseFile();
		ArrayList<Double> SR = illuminantSR();
		
		ChooseFile();
  	  	double[][] CIExyz = CIExyz();
  	  	 
  	  	double[][] xyzbar = new double[SR_Range][3];
  	  	double[] xyz = new double[3];
  	  	double[] CCTxyz = new double[3];
  	  	double[] tempxyz = new double[3];
  	  	double[][] CDxyz = new double[SR_Range][3];	//CD means Chromaticity Diagram
  	  	
  	  	for(i=0;i<SR_Range;i++){
			//System.out.printf("%d",R);
  	  		for(j=0;j<3;j++){
  	  			xyzbar[i][j] = SR.get(i)*CIExyz[i][j]*10;
  	  			//System.out.printf(" %f ", xyzbar[i][j]);
  			}
  	  		//System.out.printf("\n");
  	  		
  	  		xyz[0] += xyzbar[i][0];
			xyz[1] += xyzbar[i][1];
			xyz[2] += xyzbar[i][2];  	  		
			/*
			System.out.printf("%d",R);
  	  		for(l=0;l<3;l++){
  	  			CDxyz[i][l] = xyzbar[i][l]/(xyzbar[i][0]+xyzbar[i][1]+xyzbar[i][2]);
  	  			System.out.printf(" %f ", CDxyz[i][l]);
  	  		}
  	  		System.out.printf("\n");
			R += 10;
  	  		//System.out.printf("\n");*/
  	  	}
  	  	
  	  	//CIE Chromaticity Diagram.
  	  	JFrame frame = new JFrame("CIE Chromaticity Diagram");
  	  	JTextArea Xpanel = null,Ypanel = null;
  	  	JLabel Xlabel,Ylabel,label = null;
  	  	double w,h;
  	  	String X = "",Y = "";
  	  	
  	  	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  	frame.pack();
	  	frame.setSize(700, 850);
	  	frame.setVisible(true);
	  	
	  	//Measure Color Temperature.
  	  	tempxyz[0] = xyz[0];
  	  	tempxyz[1] = xyz[1];
  		tempxyz[2] = xyz[2];
  		
  		k = 100/xyz[1];
  		
  		//Y value
  		xyz[0] = xyz[0]/xyz[1]*100;
  		xyz[2] = xyz[2]/xyz[1]*100;
  		xyz[1] = xyz[1]/xyz[1]*100;
  		
  		System.out.printf("\n%f %f %f",xyz[0],xyz[1],xyz[2]);  		
  		//System.out.printf("\n%f\n",k);
  		
  		CCTxyz[0] = tempxyz[0]*k;
  		CCTxyz[1] = tempxyz[1]*k;
  		CCTxyz[2] = tempxyz[2]*k;
  		
  		XYZtoCorColorTemp(CCTxyz);
	}
	
  public static ArrayList<Double> illuminantSR(){
	  ArrayList SRlist = new ArrayList();
	  ArrayList<Double> SRvalue = new ArrayList<Double>();
	  ArrayList<Double> SRvalue2 = new ArrayList<Double>();
	  String line;
	  String tempString;
	  String[] tempArray;
	  String[] tempArray2 = null;
	  int R = 380;
	  
	  try{
    	  System.out.printf("%s \n", illuminant_path);
		  linesreader = new BufferedReader(new FileReader(illuminant_path));		  
		  //先分解每一行字串
		  while((line=linesreader.readLine())!=null){
			  tempString = line;
			  tempArray=tempString.split(">");
			  //i=1, 取該行後半字串
			  for(i=1;i<tempArray.length;i++){
				  //然後又再次分解字串,抓前半
				  tempArray2=tempArray[i].split("<");
				  SRlist.add(tempArray2[i-1]);	
			  }
		  }		  
		  linesreader.close();

		  SR_Range = SRlist.size();
		  System.out.printf("\n<The original data of illuminance R>");
		  for(i=0;i<SR_Range;i++){
			  SRvalue.add(Double.parseDouble((String)SRlist.get(i)));
			  System.out.printf("\n"+R+" %f", SRvalue.get(i));
			  R += 10;
		  }
		  System.out.printf("\n");
		  
		  R=380;		  
		  if(SRvalue.get(18)!=100) {		//560nm value normalize 100
			  System.out.printf("\n<560nm value normalize 100>");
			  for(i=0;i<SR_Range;i++){
				  SRvalue2.add(SRvalue.get(i)/SRvalue.get(18)*100);
				  System.out.printf("\n"+R+" %f", SRvalue2.get(i));
				  R +=10;
			  }
			  System.out.printf("\n\n");			  
		  }		  
	  }
	  catch(Exception e){
	  }
	  return SRvalue2;
  }  
  /*
  public static ArrayList<Double> illuminantSR(){
	  ArrayList SRlist = new ArrayList();
	  ArrayList<Double> SRvalue = new ArrayList<Double>();
	  String line;
	  String tempString;
	  String[] tempArray;
	  try{
    	  System.out.printf("%s \n", illuminant_path);
		  linesreader = new BufferedReader(new FileReader(illuminant_path));
		  SRlist = new ArrayList();
		  while((line=linesreader.readLine())!=null){
			  tempString = line;
			  tempArray=tempString.split("\t");	
			  for(i=1;i<tempArray.length;i++)
				  SRlist.add(tempArray[i]);	
	  }
	  
	  linesreader.close();
	  SR_Range = SRlist.size();
	  double[] SR = new double[SR_Range];
	  for(i=0;i<SR_Range;i++){
		  SRvalue.add(Double.parseDouble((String)SRlist.get(i)));
		  SR[i]=SRvalue.get(i);
	  }
	  System.out.printf("%d \n", SR_Range);
	  for(i=0;i<SR_Range;i++)
		  System.out.printf("%f \n", SR[i]);
	  }
	  catch(Exception e){
	  }
	  return SRvalue;
  }  
  */
  public static double[][] CIExyz(){
	  ArrayList CIElist = new ArrayList();
	  ArrayList<Double> CIEvalue = new ArrayList<Double>();
	  String line;
	  String tempString;
	  String[] tempArray;
	  double[][] CIExyz = null;
	  int R = 380;
	  
	  try{	  
		  System.out.printf("%s \n", CIE_path);
		  linesreader = new BufferedReader(new FileReader(CIE_path));
		  //先分解每一行字串
		  while((line=linesreader.readLine())!=null){
			  tempString = line;
			  tempArray=tempString.split("\t");	
			  //i=1, 取該行後半字串
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
		  System.out.printf("\n<The CIE matching function data>");
		  for(i=0;i<SR_Range;i++){
			  System.out.printf("%d",R);
			  for(j=0;j<3;j++){
				  System.out.printf(" %f", CIExyz[i][j]);
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
      
      if(cc==0){
    	  illuminant_path = fileName.getPath();
    	  //System.out.printf("%d\n",cc);     
      }
      if(cc==1){
    	  CIE_path = fileName.getPath();
    	  //System.out.printf("%d\n",cc);
      }
    cc++;
  }
  
  public static int XYZtoCorColorTemp(double[] CCTxyz)
  {
          double us, vs;		//色度圖  u,v 座標
          double p, di=0, dm;	//p = process, di, dm 求CCT最近距離
          int cl;				//CCT loop
          int Tc;          
          //System.out.printf("\n%f %f %f\n",CCTxyz[0],CCTxyz[1],CCTxyz[2]);
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
          
          JOptionPane.showMessageDialog(null, Tc ,"色溫測量值", JOptionPane.INFORMATION_MESSAGE);          
          return Tc;      // success 
  }
}
