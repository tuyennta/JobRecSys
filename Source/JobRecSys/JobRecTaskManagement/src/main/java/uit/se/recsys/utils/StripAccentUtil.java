package uit.se.recsys.utils;

import java.util.HashMap;

public class StripAccentUtil {

    public StripAccentUtil() {
	keysMap = new HashMap<Character, Character>();
	keysMap.put('á', 'a');
	keysMap.put('à', 'a');
	keysMap.put('ã', 'a');
	keysMap.put('ả', 'a');
	keysMap.put('ạ', 'a');
	keysMap.put('ă', 'a');
	keysMap.put('ắ', 'a');
	keysMap.put('ằ', 'a');
	keysMap.put('ẳ', 'a');
	keysMap.put('ẵ', 'a');
	keysMap.put('ặ', 'a');
	keysMap.put('â', 'a');
	keysMap.put('ấ', 'a');
	keysMap.put('ầ', 'a');
	keysMap.put('ẩ', 'a');
	keysMap.put('ẫ', 'a');
	keysMap.put('ậ', 'a');
	keysMap.put('đ', 'd');
	keysMap.put('é', 'e');
	keysMap.put('è', 'e');
	keysMap.put('ẽ', 'e');
	keysMap.put('ẻ', 'e');
	keysMap.put('ẹ', 'e');
	keysMap.put('ê', 'e');
	keysMap.put('ế', 'e');
	keysMap.put('ề', 'e');
	keysMap.put('ể', 'e');
	keysMap.put('ễ', 'e');
	keysMap.put('ệ', 'e');
	keysMap.put('í', 'i');
	keysMap.put('ì', 'i');
	keysMap.put('ỉ', 'i');
	keysMap.put('ĩ', 'i');
	keysMap.put('ị', 'i');
	keysMap.put('ó', 'o');
	keysMap.put('ò', 'o');
	keysMap.put('õ', 'o');
	keysMap.put('ỏ', 'o');
	keysMap.put('ọ', 'o');
	keysMap.put('ô', 'o');
	keysMap.put('ố', 'o');
	keysMap.put('ồ', 'o');
	keysMap.put('ổ', 'o');
	keysMap.put('ỗ', 'o');
	keysMap.put('ộ', 'o');
	keysMap.put('ơ', 'o');
	keysMap.put('ớ', 'o');
	keysMap.put('ờ', 'o');
	keysMap.put('ở', 'o');
	keysMap.put('ỡ', 'o');
	keysMap.put('ợ', 'o');
	keysMap.put('u', 'u');
	keysMap.put('ú', 'u');
	keysMap.put('ù', 'u');
	keysMap.put('ủ', 'u');
	keysMap.put('ũ', 'u');
	keysMap.put('ụ', 'u');
	keysMap.put('ư', 'u');
	keysMap.put('ứ', 'u');
	keysMap.put('ừ', 'u');
	keysMap.put('ữ', 'u');
	keysMap.put('ử', 'u');
	keysMap.put('ự', 'u');
	keysMap.put('y', 'y');
	keysMap.put('ý', 'y');
	keysMap.put('ỳ', 'y');
	keysMap.put('ỷ', 'y');
	keysMap.put('ỹ', 'y');
	keysMap.put('ỵ', 'y');
	keysMap.put('Á', 'A');
	keysMap.put('À', 'A');
	keysMap.put('Ã', 'A');
	keysMap.put('Ả', 'A');
	keysMap.put('Ạ', 'A');
	keysMap.put('Ă', 'A');
	keysMap.put('Ắ', 'A');
	keysMap.put('Ằ', 'A');
	keysMap.put('Ẳ', 'A');
	keysMap.put('Ẵ', 'A');
	keysMap.put('Ặ', 'A');
	keysMap.put('Â', 'A');
	keysMap.put('Ấ', 'A');
	keysMap.put('Ầ', 'A');
	keysMap.put('Ẩ', 'A');
	keysMap.put('Ẫ', 'A');
	keysMap.put('Ậ', 'A');
	keysMap.put('Đ', 'D');
	keysMap.put('Ê', 'E');
	keysMap.put('Ế', 'E');
	keysMap.put('Ề', 'E');
	keysMap.put('Ể', 'E');
	keysMap.put('Ễ', 'E');
	keysMap.put('Ệ', 'E');
	keysMap.put('Í', 'I');
	keysMap.put('Ì', 'I');
	keysMap.put('Ỉ', 'I');
	keysMap.put('Ĩ', 'I');
	keysMap.put('Ị', 'I');
	keysMap.put('Ó', 'O');
	keysMap.put('Ò', 'O');
	keysMap.put('Õ', 'O');
	keysMap.put('Ỏ', 'O');
	keysMap.put('Ọ', 'O');
	keysMap.put('Ô', 'O');
	keysMap.put('Ố', 'O');
	keysMap.put('Ồ', 'O');
	keysMap.put('Ổ', 'O');
	keysMap.put('Ỗ', 'O');
	keysMap.put('Ộ', 'O');
	keysMap.put('Ơ', 'O');
	keysMap.put('Ớ', 'O');
	keysMap.put('Ờ', 'O');
	keysMap.put('Ở', 'O');
	keysMap.put('Ỡ', 'O');
	keysMap.put('Ợ', 'O');
	keysMap.put('U', 'U');
	keysMap.put('Ú', 'U');
	keysMap.put('Ù', 'U');
	keysMap.put('Ủ', 'U');
	keysMap.put('Ũ', 'U');
	keysMap.put('Ụ', 'U');
	keysMap.put('Ư', 'U');
	keysMap.put('Ứ', 'U');
	keysMap.put('Ừ', 'U');
	keysMap.put('Ữ', 'U');
	keysMap.put('Ử', 'U');
	keysMap.put('Ự', 'U');
	keysMap.put('Y', 'Y');
	keysMap.put('Ý', 'Y');
	keysMap.put('Ỳ', 'Y');
	keysMap.put('Ỷ', 'Y');
	keysMap.put('Ỹ', 'Y');
	keysMap.put('Ỵ', 'Y');

    }

    private HashMap<Character, Character> keysMap;

    public String convert(String src) {
	String results = src;
	for (char c : keysMap.keySet()) {
	    results = results.replace(c, keysMap.get(c));
	}
	return results;
    }

}