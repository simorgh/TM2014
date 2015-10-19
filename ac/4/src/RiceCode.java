/**
 *
 * @author Igor
 */
public class RiceCode {
    
    /**
     * 
     * @param Data the integer to compress
     * @param M compression param
     * @return  the compressed binary secuence parsed to string 
     */
    public RiceCode(){}
            
    public static String rice_compress(int Data, int M){
        
        StringBuilder result = new StringBuilder();
        if(! ((M!=0)&&((M&(M-1))==0))) return "";
        else{
            int pr = (int)(Math.log(M)/Math.log(2))-1;
            int Q = (Math.abs(Data)/M);
            int R = (Math.abs(Data)%M);
            //tratamos el signo
            if(Data < 0) result.append("0");
            else result.append("1");
            
            //agregamos el cociente
            for(int i = 0; i < Q; i++) result.append("1");
            result.append("0");
            //finalmente agregamos el residuo
            result.append(int2bin(R,M));
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
        return Q*M + R;

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


    
}
