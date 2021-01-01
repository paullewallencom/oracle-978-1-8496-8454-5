package oracle.cep.example.jaxb.customizer;

import javax.xml.bind.DatatypeConverter;

public class JaxbXmlDateTimeCustomizer {

	public static long xmlDateTimeToLong(String dateTimeAsString) {
		java.util.Calendar calendar = DatatypeConverter.parseDateTime(dateTimeAsString);
		return calendar.getTimeInMillis();
	}

	public static String longToXmlDateTime(long dateTimeAsMs) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTimeInMillis(dateTimeAsMs);
		return DatatypeConverter.printDateTime(calendar);
	}
}
