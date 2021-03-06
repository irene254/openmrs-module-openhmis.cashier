package org.openmrs.module.openhmis.cashier.api.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.openhmis.cashier.api.model.CashierOptions;
import org.openmrs.module.openhmis.cashier.web.CashierWebConstants;
import org.openmrs.module.openhmis.inventory.api.IItemDataService;
import org.openmrs.module.openhmis.inventory.api.model.Item;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class CashierOptionsServiceGpImplTest {
	private CashierOptionsServiceGpImpl optionsService = null;
	private AdministrationService adminService = null;
	private IItemDataService itemService = null;

	@Before
	public void before() {
		adminService = mock(AdministrationService.class);
		itemService = mock(IItemDataService.class);

		optionsService = new CashierOptionsServiceGpImpl(adminService, itemService);
	}

	/**
	 * @verifies load cashier options from the database
	 * @see CashierOptionsServiceGpImpl#getOptions()
	 */
	@Test
	public void getOptions_shouldLoadCashierOptionsFromTheDatabase() throws Exception {
		when(adminService.getGlobalProperty(CashierWebConstants.RECEIPT_REPORT_ID_PROPERTY))
				.thenReturn("1");
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_MODE_PROPERTY))
				.thenReturn(CashierOptions.RoundingMode.MID.toString());
		when(adminService.getGlobalProperty(CashierWebConstants.ROUND_TO_NEAREST_PROPERTY))
				.thenReturn("5");
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_ITEM_ID))
				.thenReturn("1");
		when(adminService.getGlobalProperty(CashierWebConstants.TIMESHEET_REQUIRED_PROPERTY))
				.thenReturn("true");

		Item item = new Item();
		when(itemService.getById(1))
				.thenReturn(item);

		CashierOptions options = optionsService.getOptions();

		Assert.assertNotNull(options);
		Assert.assertEquals(1, options.getDefaultReceiptReportId());
		Assert.assertEquals(CashierOptions.RoundingMode.MID, options.getRoundingMode());
		Assert.assertEquals(new BigDecimal(5), options.getRoundToNearest());
		Assert.assertEquals(item.getUuid(), options.getRoundingItemUuid());
		Assert.assertEquals(true, options.isTimesheetRequired());
	}

	/**
	 * @verifies not throw exception if numeric options are null
	 * @see CashierOptionsServiceGpImpl#getOptions()
	 */
	@Test
	public void getOptions_shouldNotThrowExceptionIfNumericOptionsAreNull() throws Exception {
		when(adminService.getGlobalProperty(CashierWebConstants.RECEIPT_REPORT_ID_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_MODE_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUND_TO_NEAREST_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_ITEM_ID))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.TIMESHEET_REQUIRED_PROPERTY))
				.thenReturn(null);

		CashierOptions options = optionsService.getOptions();

		Assert.assertNotNull(options);
	}

	/**
	 * @verifies default to false if timesheet required is not specified
	 * @see CashierOptionsServiceGpImpl#getOptions()
	 */
	@Test
	public void getOptions_shouldDefaultToFalseIfTimesheetRequiredIsNotSpecified() throws Exception {
		when(adminService.getGlobalProperty(CashierWebConstants.RECEIPT_REPORT_ID_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_MODE_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUND_TO_NEAREST_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_ITEM_ID))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.TIMESHEET_REQUIRED_PROPERTY))
				.thenReturn(null);

		CashierOptions options = optionsService.getOptions();

		Assert.assertNotNull(options);
		Assert.assertEquals(false, options.isTimesheetRequired());
	}

	/**
	 * @verifies log Error if Exception due to non-parsable rounding item id
	 * @see CashierOptionsServiceGpImpl#getOptions()
	 */
	@Test
	public void getOptions_shouldLogErrorIfRoundingItemIdCannotBeParsed() throws Exception {

		when(adminService.getGlobalProperty(CashierWebConstants.RECEIPT_REPORT_ID_PROPERTY))
				.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_MODE_PROPERTY))
				.thenReturn(CashierOptions.RoundingMode.FLOOR.toString());
		when(adminService.getGlobalProperty(CashierWebConstants.ROUND_TO_NEAREST_PROPERTY))
				.thenReturn("5");
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_ITEM_ID))
				.thenReturn("HELP");

		Logger logger = Logger.getLogger(CashierOptionsServiceGpImpl.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Layout layout = new SimpleLayout();
        Appender appender = new WriterAppender(layout, out);
        logger.addAppender(appender);

        try {
        	optionsService.getOptions();
            String logMsg = out.toString();
            assertNotNull(logMsg);
            assertFalse((logMsg.trim()).equals(""));
        } finally {
            logger.removeAppender(appender);
        }
	}

	/**
	 * @verifies log error if rouding item id is set but item cannot be found (and hence is null)
	 * @see CashierOptionsServiceGpImpl#getOptions()
	 */
	@Test
	public void getOptions_shouldLogErrorIfRoundingItemIsNullDespiteIdGiven() throws Exception {

		when(adminService.getGlobalProperty(CashierWebConstants.RECEIPT_REPORT_ID_PROPERTY))
			.thenReturn(null);
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_MODE_PROPERTY))
			.thenReturn(CashierOptions.RoundingMode.FLOOR.toString());
		when(adminService.getGlobalProperty(CashierWebConstants.ROUND_TO_NEAREST_PROPERTY))
			.thenReturn("5");
		when(adminService.getGlobalProperty(CashierWebConstants.ROUNDING_ITEM_ID))
			.thenReturn("273423");
		when(adminService.getGlobalProperty(CashierWebConstants.TIMESHEET_REQUIRED_PROPERTY))
			.thenReturn(null);

		Logger logger = Logger.getLogger(CashierOptionsServiceGpImpl.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Layout layout = new SimpleLayout();
        Appender appender = new WriterAppender(layout, out);
        logger.addAppender(appender);

        try {
        	optionsService.getOptions();
            String logMsg = out.toString();
            assertNotNull(logMsg);
            assertFalse((logMsg.trim()).equals(""));
        } finally {
            logger.removeAppender(appender);
        }
	}

}
