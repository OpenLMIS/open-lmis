package org.openlmis.core.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

/**
 * A StockAdjustmentReason object is a value object that is a reason for a stock adjustment.  i.e. a credit or debit
 * to stock on hand, a transfer, etc.  Most importantly, a stock adjustment reason is determined to be either
 * additive or not.  When contained within a stock adjustment, this reason's additive quality would be used to
 * determine if the quantity of stock adjusted, when summed, contributes to that summation in an additive (+) or
 * subtractive (-) manner.
 *
 * When determining which reasons are available in different areas of the program, the properties {@link #isDefault}
 * and {@link #inCategory(Category)} are useful.  If a reason is a default reason, it is intended to be used across
 * programs, see {@link StockAdjustmentReasonProgram}.  When a reason is in a {@link Category}, the reason is
 * intended to be used programmatically in that section of the program.
 */
@EqualsAndHashCode(callSuper=false)
public class StockAdjustmentReason extends BaseModel implements Importable {

  @Getter
  @ImportField(mandatory = true, name = "Reason Name")
  private String name;

  @Getter
  @ImportField(name = "Description")
  private String description;

  @Getter
  @Setter
  @ImportField(mandatory = true, name = "Additive", type = "boolean")
  private Boolean additive;

  @Setter
  @ImportField(mandatory = true, type = "int", name = "Display Order")
  private Integer displayOrder;

  @Setter
  @ImportField(name = "Is Default", type = "boolean")
  private Boolean isDefault;

  @ImportField(name = "Category", type = "String")
  private String category;

  @Deprecated
  public StockAdjustmentReason() {
    setCategoryHelper(Category.DEFAULT); // useful for upload configuration, would prefer validity pattern in future
  }

  /**
   * Create a new StockAdjustmentReason.
   * @param name the globally unique name
   * @param description a description, the {@link #name} if null.
   * @param additive if additive then stock adjustments with this reason result in additive summations.
   * @param displayOrder the order this appears in lists with other StockAdjustmentReason
   * @param isDefault if true then this reason is a default reason for all programs.
   *                  See {@link StockAdjustmentReasonProgram}.
   * @param category the {@link org.openlmis.core.domain.StockAdjustmentReason.Category} this reason is used within.
   *                 {@link org.openlmis.core.domain.StockAdjustmentReason.Category#DEFAULT} if null.
   * @throws IllegalArgumentException if name is null or blank.
   */
  private StockAdjustmentReason(String name,
                               String description,
                               boolean additive,
                               int displayOrder,
                               boolean isDefault,
                               Category category) {
    setName(name);
    setDescription(description);
    this.additive = additive;
    this.displayOrder = displayOrder;
    this.isDefault = isDefault;
    setCategoryHelper(category);
  }

  /**
   * Creates a new reason with the given name.  See {@link #create(String, String)}.
   * @param name the name of the reason, will also be the description.
   * @return a new reason.
   * @throws IllegalArgumentException if name is blank.
   */
  public static StockAdjustmentReason create(String name) {
    return create(name, name);
  }

  /**
   * Creates an additive, default reason in the {@link org.openlmis.core.domain.StockAdjustmentReason.Category#DEFAULT}
   * category with a "low" display order".
   * @param name the name.  See {@link #setName(String)}.
   * @param description the description. See {@link #setDescription(String)}.
   * @return a new reason
   * @throws IllegalArgumentException if name is blank.
   */
  public static StockAdjustmentReason create(String name, String description) {
    return new StockAdjustmentReason(name,
      description,
      true,
      0,
      true,
      Category.DEFAULT);
  }

  /**
   * Sets the globally unique name for this reason
   * @param name the name
   * @throws IllegalArgumentException if name is blank
   */
  public void setName(String name) {
    if (false == StringUtils.isBlank(name))
      this.name = name.trim();
    else
      throw new IllegalArgumentException("Name can't be blank");
  }

  /**
   * Sets the description for this reason, or uses the name if blank.
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = StringUtils.isBlank(description) ? this.name : description.trim();
  }

  /**
   * Sets the {@link Category} that this reason is used within.
   * @param category the category to use.
   * @throws NullPointerException if category is unrecognized
   */
  private void setCategoryHelper(Category category) {
    this.category = (null == category ? Category.DEFAULT : category).toString();
  }

  /**
   * Sets the {@link org.openlmis.core.domain.StockAdjustmentReason.Category} by parsing the given string.  Valid
   * values are of the form {@link Category#values()}.
   * @param category the category to set.
   * @throws NullPointerException if category is unrecognized.
   */
  public void setCategory(String category) {
    setCategoryHelper(Category.parse(category));
  }

  /**
   * Determines if this reason is a part of the given {@link org.openlmis.core.domain.StockAdjustmentReason.Category}
   * @param category the category
   * @return true if apart of the given category, false otherwise.
   */
  public boolean inCategory(Category category) {
    if(null == category) return false;
    return getCategory() == category;
  }

  protected final Category getCategory() {
    return Category.parse(category);
  }

  /**
   * A category concept to {@link StockAdjustmentReason} allow a group of reasons to be grouped programatically
   * for identifying a reason for specific functionalities of the application (regardless of other groupings).  e.g.
   * some reasons may only be applicable to a feature that deals with national arrival of commodities whereas another
   * section may only be concerned with the daily transactions at a rural service delivery point.
   */
  public enum Category {
    DEFAULT,
    NATIONAL_ARRIVAL;

    /**
     * Parses the given String to return a matching Category using {@link #values()}.  Cleans the string first
     * of whitespace and case-sensitivity.
     * @param category the category string to parse
     * @return the Category that matches the string, null if no String parses to a Category.
     */
    public static Category parse(String category) {
      String wCat = StringUtils.trimToEmpty(category).toUpperCase();
      Category cat = null;
      try {
        cat = valueOf(wCat);
      } catch (IllegalArgumentException iae) {}
      return cat;
    }
  }
}
