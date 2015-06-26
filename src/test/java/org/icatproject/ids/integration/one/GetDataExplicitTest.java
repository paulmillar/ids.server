package org.icatproject.ids.integration.one;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Arrays;

import org.icatproject.Datafile;
import org.icatproject.ids.integration.BaseTest;
import org.icatproject.ids.integration.util.Setup;
import org.icatproject.ids.integration.util.client.BadRequestException;
import org.icatproject.ids.integration.util.client.DataSelection;
import org.icatproject.ids.integration.util.client.InsufficientPrivilegesException;
import org.icatproject.ids.integration.util.client.TestingClient.Flag;
import org.junit.BeforeClass;
import org.junit.Test;

public class GetDataExplicitTest extends BaseTest {

	@BeforeClass
	public static void setup() throws Exception {
		setup = new Setup("one.properties");
		icatsetup();
	}

	@Test
	public void getSizes() throws Exception {
		Datafile df = null;
		Long size = 0L;
		try {
			df = (Datafile) icat.get(sessionId, "Datafile INCLUDE 1", datafileIds.get(0));
			size = df.getFileSize();
			df.setFileSize(size + 1);
			icat.update(sessionId, df);
			assertEquals(209L, testingClient.getSize(sessionId, new DataSelection().addDatafiles(datafileIds), 200));
		} finally {
			if (df != null) {
				df.setFileSize(size);
				icat.update(sessionId, df);
			}
		}
	}

	@Test(expected = BadRequestException.class)
	public void badPreparedIdFormatTest() throws Exception {
		try (InputStream z = testingClient.getData("bad preparedId format", 0, 400)) {
		}
	}

	@Test(expected = InsufficientPrivilegesException.class)
	public void forbiddenTest() throws Exception {
		try (InputStream z = testingClient.getData(setup.getForbiddenSessionId(),
				new DataSelection().addDatafiles(datafileIds), Flag.NONE, 0, 403)) {
		}
	}

	@Test
	public void correctBehaviourTestNone() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafiles(datafileIds),
				Flag.NONE, 0, 200)) {
			checkZipStream(stream, datafileIds, 57L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.NONE, 0, 200)) {
			checkStream(stream, datafileIds.get(0));
		}
	}

	@Test
	public void correctBehaviourTestCompress() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafiles(datafileIds),
				Flag.COMPRESS, 0, 200)) {
			checkZipStream(stream, datafileIds, 36L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.COMPRESS, 0, 200)) {
			checkStream(stream, datafileIds.get(0));
		}
	}

	@Test
	public void correctBehaviourTestZip() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafiles(datafileIds),
				Flag.ZIP, 0, 200)) {
			checkZipStream(stream, datafileIds, 57L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.ZIP, 0, 200)) {
			checkZipStream(stream, datafileIds.subList(0, 1), 57L);
		}
	}

	@Test
	public void correctBehaviourTestZipAndCompress() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafiles(datafileIds),
				Flag.ZIP_AND_COMPRESS, 0, 200)) {
			checkZipStream(stream, datafileIds, 36L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.ZIP_AND_COMPRESS, 0, 200)) {
			checkZipStream(stream, datafileIds.subList(0, 1), 36L);
		}
	}

	@Test
	public void correctBehaviourInvestigation() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId,
				new DataSelection().addInvestigation(investigationId), Flag.NONE, 0, 200)) {
			checkZipStream(stream, datafileIds, 57L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.ZIP, 0, 200)) {
			checkZipStream(stream, datafileIds.subList(0, 1), 57L);
		}
	}

	@Test
	public void correctBehaviourInvestigations() throws Exception {
		try (InputStream stream = testingClient.getData(sessionId,
				new DataSelection().addInvestigations(Arrays.asList(investigationId)), Flag.NONE, 0, 200)) {
			checkZipStream(stream, datafileIds, 57L);
		}

		try (InputStream stream = testingClient.getData(sessionId, new DataSelection().addDatafile(datafileIds.get(0)),
				Flag.ZIP, 0, 200)) {
			checkZipStream(stream, datafileIds.subList(0, 1), 57L);
		}
	}

}
