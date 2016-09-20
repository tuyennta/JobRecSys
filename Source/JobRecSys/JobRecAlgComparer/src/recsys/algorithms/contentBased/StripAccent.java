package recsys.algorithms.contentBased;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class StripAccent {

	private boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String removeStopWord(String data) {
		data = data.toLowerCase();
		data = data.replace(',', ' ');
		data = data.replace('-', ' ');
		String[] words = StringUtils.split(data, ' ');
		StringBuilder rs = new StringBuilder();
		for (String w : words) {
			String wt = w.trim();
			if (!listBags.contains(wt) || isNumber(wt)||wt.contains("/")) {
				rs.append(wt + " ");
			}
		}
		return rs.toString();
	}

	public StripAccent() {

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

		keysMap.put('/', ' ');
		keysMap.put('-', ' ');
		keysMap.put('+', ' ');
		keysMap.put('.', ' ');
		keysMap.put(',', ' ');
		keysMap.put('(', ' ');
		keysMap.put(')', ' ');
		keysMap.put(':', ' ');
		keysMap.put(';', ' ');
		keysMap.put('!', ' ');
		keysMap.put('?', ' ');
		keysMap.put('&', ' ');
		keysMap.put('\n', ' ');
		keysMap.put('\t', ' ');
		keysMap.put('\r', ' ');
		keysMap.put('>', ' ');
		keysMap.put('<', ' ');
		keysMap.put('’', ' ');
		//

		String[] bag = { "nhận", "rằng", "cao", "nhà", "quá", "riêng", "gì", "muốn", "rồi", "số", "thấy", "hay", "lên",
				"lần", "nào", "qua", "bằng", "điều", "biết", "lớn", "khác", "vừa", "nếu", "thời gian", "họ", "từng",
				"đây", "tháng", "trước", "chính", "cả", "việc", "chưa", "do", "nói", "ra", "nên", "đều", "đi", "tới",
				"tôi", "có thể", "cùng", "vì", "làm", "lại", "mới", "ngày", "đó", "vẫn", "mình", "chỉ", "thì", "đang",
				"còn", "bị", "mà", "năm", "nhất", "hơn", "sau", "ông", "rất", "anh", "phải", "như", "trên", "tại",
				"theo", "khi", "nhưng", "vào", "đến", "nhiều", "người", "từ", "sẽ", "ở", "cũng", "không", "về", "để",
				"này", "những", "một", "các", "cho", "được", "với", "có", "trong", "đã", "là", "và", "của", "thực sự",
				"ở trên", "tất cả", "dưới", "hầu hết", "luôn", "giữa", "bất kỳ", "hỏi", "bạn", "cô", "tôi", "tớ", "cậu",
				"bác", "chú", "dì", "thím", "cậu", "mợ", "ông", "bà", "em", "thường", "ai", "cảm ơn", "nhận", "rằng",
				"cao", "nhà", "quá", "riêng", "gì", "muốn", "rồi", "số", "thấy", "hay", "lên", "lần", "nào", "qua",
				"bằng", "điều", "biết", "lớn", "khác", "vừa", "nếu", "thời gian", "họ", "từng", "đây", "tháng", "trước",
				"chính", "cả", "việc", "chưa", "do", "nói", "ra", "nên", "đều", "đi", "tới", "tôi", "có thể", "cùng",
				"vì", "làm", "lại", "mới", "ngày", "đó", "vẫn", "mình", "chỉ", "thì", "đang", "còn", "bị", "mà", "năm",
				"nhất", "hơn", "sau", "ông", "rất", "anh", "phải", "như", "trên", "tại", "theo", "khi", "nhưng", "vào",
				"đến", "nhiều", "người", "từ", "sẽ", "ở", "cũng", "không", "về", "để", "này", "những", "một", "các",
				"cho", "được", "với", "có", "trong", "đã", "là", "và", "của", "thực sự", "ở trên", "tất cả", "dưới",
				"hầu hết", "luôn", "giữa", "bất kỳ", "hỏi", "bạn", "cô", "tôi", "tớ", "cậu", "bác", "chú", "dì", "thím",
				"cậu", "mợ", "ông", "bà", "em", "thường", "ai", "cảm ơn", "bị", "bởi", "cả", "các", "cái", "cần",
				"càng", "chỉ", "chiếc", "cho", "chứ", "chưa", "chuyện", "có", "có_thể", "cứ", "của", "cùng", "cũng",
				"đã", "đang", "đây", "để", "đến_nỗi", "đều", "điều", "do", "đó", "được", "dưới", "gì", "khi", "không",
				"là", "lại", "lên", "lúc", "mà", "mỗi", "một_cách", "này", "nên", "nếu", "ngay", "nhiều", "như",
				"nhưng", "những", "nơi", "nữa", "phải", "qua", "ra", "rằng", "rằng", "rất", "rất", "rồi", "sau", "sẽ",
				"so", "sự", "tại", "theo", "thì", "trên", "trước", "từ", "từng", "và", "vẫn", "vào", "vậy", "vì",
				"việc", "với", "vừa", "null", "for", "is", "are", "of", "in", "at", "so", "least", "year", "or", "with",
				"able", "to", "new", "before", "after", "any", "a", "able", "about", "across", "after", "all", "almost",
				"also", "am", "among", "an", "and", "any", "are", "as", "at", "be", "because", "been", "but", "by",
				"can", "cannot", "could", "dear", "did", "do", "does", "either", "else", "ever", "every", "for", "from",
				"get", "got", "had", "has", "have", "he", "her", "hers", "him", "his", "how", "however", "i", "if",
				"in", "into", "is", "it", "its", "just", "least", "let", "like", "likely", "may", "me", "might", "most",
				"must", "my", "neither", "no", "nor", "not", "of", "off", "often", "on", "only", "or", "other", "our",
				"own", "rather", "said", "say", "says", "she", "should", "since", "so", "some", "than", "that", "the",
				"their", "them", "then", "there", "these", "they", "this", "tis", "to", "too", "twas", "us", "wants",
				"was", "we", "were", "what", "when", "where", "which", "while", "who", "whom", "why", "will", "with",
				"would", "yet", "you", "your","skill","education" };
		listBags = new ArrayList<String>();
		for (String i : bag) {
			listBags.add(i);
		}
	}

	private ArrayList<String> listBags;
	private HashMap<Character, Character> keysMap;

	public String convert(String src) {
		String results = src;
		for (char c : keysMap.keySet()) {
			results = StringUtils.replaceChars(results, c, keysMap.get(c));
		}
		return results;
	}
	
	public String removeComma(String src) {
		
		HashMap<Character, Character> _keysMap = new HashMap<Character, Character>();
		_keysMap.put('/', ' ');
		_keysMap.put('-', ' ');
		_keysMap.put('+', ' ');
		_keysMap.put('.', ' ');
		_keysMap.put(',', ' ');
		_keysMap.put('(', ' ');
		_keysMap.put(')', ' ');
		_keysMap.put(':', ' ');
		_keysMap.put(';', ' ');
		_keysMap.put('!', ' ');
		_keysMap.put('?', ' ');
		_keysMap.put('&', ' ');
		_keysMap.put('\n', ' ');
		_keysMap.put('\t', ' ');
		_keysMap.put('\r', ' ');
		_keysMap.put('>', ' ');
		_keysMap.put('<', ' ');
		_keysMap.put('’', ' ');
		_keysMap.put(']', ' ');
		_keysMap.put('[', ' ');
		
		String results = src;
		for (char c : _keysMap.keySet()) {
			results = StringUtils.replaceChars(results, c, _keysMap.get(c));
		}
		return results;
	}
}
