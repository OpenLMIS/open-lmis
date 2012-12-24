package org.openlmis.web.configurationReader;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
public class StaticReferenceDataReader {
	@Value("${currencySymbol}")
	private String currency;

	public String getCurrency() {
		return currency;
	}
}
