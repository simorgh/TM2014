import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Igor
 */
public class LZ77Rice {
    private static int mEnt; //tamaño de la ventana de entrada
    private static int mDest;//tamaño de la ventana deslizante
    private static int bytesDist;// Math.log(mDest) / Math.log(2);
    private static int bytesLong;// Math.log(mEnt) / Math.log(2);
    private static int dataSize;
    private static String secuence;
    static txtReader wr;
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String ruta = "";
        File f;
        String choice;
        String compr = "";
        int[] data;
        
        Scanner scan = new Scanner(System.in);
        String c = rice_compress(2000,256);
        System.out.println("Rice compression of 2000 with   M = 256 is "+c);
        System.out.println("Rice decompression of " + c+" is "+rice_decompress(c));
       
        do {
            System.out.print("Que quieres hacer?\n\"c\" para comprimir\n\"d\" para descomprimir\n\"q\" para salir\n");
            choice = scan.nextLine();
            switch (choice) {
				case "c":
					do {
						System.out.print("Entra la ruta del fichero de texto: ");

							try {
								ruta = br.readLine();

							} catch (Exception ex) {
								 Logger.getLogger(LZ77_TM.class.getName()).log(Level.SEVERE, null, ex);
								 ruta = "";

							}
							 f = new File(ruta);
					} while(!f.exists());


					data= WavReader.Wav2Array(ruta);
					StringBuffer converted = new StringBuffer();
					int mval = (int) Math.pow(2, 16);
					for (int i = 0; i < data.length; i++) {
					   converted.append( int2bin(data[i],mval));
					}
					secuence = converted.toString();
					dataSize = secuence.length();

					do {
						System.out.print("Entra la longitud de ventana deslizante: ");
						while (!sc.hasNextInt()) {
							sc.next();
						}
						mDest = sc.nextInt();
						if(mDest > dataSize/2 ||!((mDest > 0) && ((mDest & (mDest - 1)) == 0))){
							System.out.println("Longitud incorrecta. Debe entrar un entero positivo, potencia de dos que no supera la mitad del tamaño de la longitud de datos\n");
						}
					} while (mDest > dataSize/2 || !((mDest > 0) && ((mDest & (mDest - 1)) == 0)));


					do {
						System.out.print("Entra la longitud de ventana de entrada: ");
						while (!sc.hasNextInt()) {
							sc.next();
						}
						mEnt = sc.nextInt();
						if(mEnt > mDest || !((mEnt > 0) && ((mEnt & (mEnt - 1)) == 0))){
							System.out.println("Longitud incorrecta. Debe entrar un entero positivo, potencia de dos que no supere el tamaño de la longitud de ventana deslizante\n");
						}
					} while (mDest+mEnt > dataSize || mEnt > mDest ||!((mEnt > 0) && ((mEnt & (mEnt - 1)) == 0)));
					System.out.println("Datos recibidos:\n\nLongitud de datos: "+dataSize+"\n"
							+ "Longitud de ventana deslizante: "+mDest +"\n"
							+"Longitud de ventana de entrada: " + mEnt+"\n");  
					System.out.println("Comprimiendo datos...");

					bytesDist =   (int)(Math.log(mDest)/Math.log(2))-1; //sirve para poner formato a los datos codificados (distancia)
					bytesLong =   (int)(Math.log(mEnt)/Math.log(2))-1;  
					long t1 = System.nanoTime();
					compr = compress(secuence,mEnt,mDest);
					t1 = System.nanoTime() - t1;
					
					System.out.println("La longitud de secuencia comprimida es:\n"+compr.length());
					System.out.println("Ratio de compresion \n"+(float)(secuence.length())/(float)(compr.length()));
					System.out.println("Se ha tardado en comprimir unos "+ ((float)t1/(float)1000000000)+" segundos");
					break;
					
				case "d":
					if(!compr.isEmpty()){
						System.out.println("Descomprimiendo datos...");
						long t2 = System.nanoTime();
						String result = decompress(compr);
						t2 = System.nanoTime() - t2;
						System.out.println("La secuencia original tiene:\n"+result.length()+" bits");
						System.out.println("Se ha tardado en descomprimir unos "+ ((float)t2/(float)1000000000)+" segundos");
					}
					break;
        
            } // end of switch
        } while (!choice.equals("q")); // end of loop 
    }

    static String compress(String data, int ment,int mdest) {
        String result = "";
        String mdes, men;
       
        int pos = 0;
        int lastPos = data.length();
        result+= data.substring(0, mdest); //compression saved initial sliding window values
        
        while (pos < lastPos){
            if (data.substring(pos + mdest).length() < ment){
				result += data.substring(pos+mdest);  // si el tamaño de la cadena restante es menor que la ventana de entrada concatenamos el resto  de la cadena
				return result;
            }
            else{
                mdes = data.substring(pos,pos+mdest);
                men = data.substring(pos + mdest, pos + mdest+ment);

                int index = -1;
                String str1 = men;
                while (index == -1 && str1.length() > 0){
                    index = mdes.lastIndexOf(str1);
                    if(index == -1){
                        str1 = str1.substring(0, str1.length()-1);
                    }

                }
                if (index!=-1){
                    result+= codificar(str1.length(),mdes.length()-index);
                    pos += str1.length();
                }
                else{
					pos += 1;
                }
            }
         }
        return result;
    }
    
    private static String codificar( int length, int distancia) {
        String len = int2bin(length,mEnt-1);
        String dist = int2bin(distancia,mDest-1);
        return len+dist;
    }
        
        
    /**
     * @param valor numero entero a codificar en binario natural
     * @param maxval valor del màximo entero codificable (determina el número de bits con que se codificara valor)
     * @return output cadena binaria al código binario natural de valor
     */
    public static String int2bin(int valor, int maxval) {
        int numbits = getNumBits(maxval);
        String binstring = Integer.toBinaryString((1 << 31) | (valor));
        return (binstring.substring(binstring.length() - numbits));
    }
	
    /**
     * @param valor número entero
     * @return output número de bits necesarios para codificar entero en binario natural
     */
    public static int getNumBits(int valor) {
        return (Integer.toBinaryString(valor).length());
    }

    static String decompress(String dades){
        String output = "";
        int pos = 0;
        int maxLen = dades.length();
        output += dades.substring(0, mDest); //copiamos el contenido del tamaño de la ventana deslizante
        pos +=mDest; 
        while (pos < dades.length()){
            if (pos+bytesLong+bytesDist < maxLen){
                String longitud = dades.substring(pos, pos+bytesLong);
                pos+=bytesLong;
                String distancia = dades.substring(pos,pos+bytesDist);
                pos +=bytesDist;
                if(! longitud.contains("1")){
                    longitud = "1"+longitud;
                }
                if(! distancia.contains("1")){
                    distancia = "1"+distancia;
                }
                int lon = Integer.parseInt(longitud, 2);
                int dis = Integer.parseInt(distancia,2);
                output += output.substring(output.length()- dis, output.length()- dis + lon);
            }
            else{
                output+=dades.substring(pos);
                pos = dades.length();
            }
        }
        return output;
    }
    
	public static String rice_compress(int Data, int M){
        StringBuilder result = new StringBuilder();
        if(! ((M!=0)&&((M&(M-1))==0))) return "";
        else{
           
            int Q = (Math.abs(Data)/M);
            int R = (Math.abs(Data)%M);
            //tratamos el signo
            if(Data < 0) result.append("0");
            else result.append("1");
            
            //agregamos el cociente
            for(int i = 0; i < Q; i++) result.append("1");
            result.append("0");
            //finalmente agregamos el residuo
            result.append(int2bin(R,M-1));
        }
        return result.toString();
    }
	
    public static int rice_decompress(String data){
        int result = 0;
		
        //String Q ;
        int sign = Integer.parseInt(data.substring(0,1));
        int pos = 1;
        int Q =0;
        while(data.charAt(pos+Q) == '1')Q++;
        pos +=Q+1;
        int R = Integer.parseInt(data.substring(pos), 2);
        int M = (int) Math.pow(2, data.substring(pos).length());
        if(sign == 1)return Q*M + R;
        else return (Q*M + R)*-1;

    }
}
