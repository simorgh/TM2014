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
public class LZ77 {
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
        String compressedSecuence = "", decompressedSecuence="";
        
        Scanner scan = new Scanner(System.in);
        do {
            System.out.print("Que quieres hacer? \n\"c\" para comprimir\n\"d\" para descomprimir\n\"q\" para salir\n");
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


					secuence = txtReader.cargarTxt(ruta).toString();
					dataSize = secuence.length();

					do {
						System.out.print("Entra la longitud de ventana deslizante: ");
						while (!sc.hasNextInt()) {
							sc.next();
						}
						mDest = sc.nextInt();
						if(mDest > dataSize ||!((mDest > 0) && ((mDest & (mDest - 1)) == 0))){
							System.out.println("Longitud incorrecta. Debe entrar un entero positivo, potencia de dos que no supere el tamaño de la longitud de datos\n");
						}
					} while (mDest > dataSize || !((mDest > 0) && ((mDest & (mDest - 1)) == 0)));


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

					bytesDist =   (int)(Math.log(mDest)/Math.log(2)); //sirve para poner formato a los datos codificados (distancia)
					bytesLong =   (int)(Math.log(mEnt)/Math.log(2));  
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
						String result = decompress(compr);
						System.out.println("La secuencia original tiene:\n"+result.length()+" bits");
					}
			
					break;
            } // end of switch
			
        } while (!choice.equals("q")); // end of loop
        
         
        System.out.println("Descomprimiendo datos...");
        String result = decompress(compressedSecuence);
        System.out.println("La secuencia original es:\n"+result);
        
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
}
