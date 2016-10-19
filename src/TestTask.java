package visualiser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(JUnit4.class)
public class TestTask {
	private static final String logsSingleUserEvent =
			"user_event {\n" +
			"    element : \"input\"\n" +
			"    element : \"div\"\n" +
			"    element : \"div\"\n" +
			"    timestamp : 0\n" +
			"} user_event[0]\n";
	private static final String logsTwoUserEvents =
			"user_event {\n" +
			"    element : \"input\"\n" +
			"    element : \"div\"\n" +
			"    element : \"div\"\n" +
			"    timestamp : 0\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    element : \"card\"\n" +
			"    element : \"div\"\n" +
			"    timestamp : 1\n" +
			"} user_event[1]\n";
	private static final String logsMultipleUserEvents_allAreas =
			"user_event {\n" +
			"    element : \"input\"\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    element : \"icon\"\n" +
			"} user_event[1]\n" +
			"user_event {\n" +
			"    element : \"dialog\"\n" +
			"} user_event[2]\n" +
			"user_event {\n" +
			"    element : \"div\"\n" +
			"} user_event[3]\n";
	private static final String logsMultipleUserEvents_oneAreaMissing =
			"user_event {\n" +
			"    element : \"input\"\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    element : \"icon\"\n" +
			"} user_event[1]\n" +
			"user_event {\n" +
			"    element : \"dialog\"\n" +
			"} user_event[2]\n";
	private static final String logsMultipleUserEvents_twoMenus =
			"user_event {\n" +
			"    element : \"input\"\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    element : \"icon\"\n" +
			"} user_event[1]\n" +
			"user_event {\n" +
			"    element : \"dialog\"\n" +
			"} user_event[2]\n" +
			"user_event {\n" +
			"    element : \"menu-button\"\n" +
			"} user_event[3]\n";
	private static final String frequentLogs_zeroBased = 
			"user_event {\n" +
			"    timestamp : 0\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    timestamp : 1\n" +
			"} user_event[1]\n" +
			"user_event {\n" +
			"    timestamp : 5\n" +
			"} user_event[2]\n" +
			"user_event {\n" +
			"    timestamp : 15\n" +
			"} user_event[3]\n" +
			"user_event {\n" +
			"    timestamp : 16\n" +
			"} user_event[4]\n" +
			"user_event {\n" +
			"    timestamp : 19\n" +
			"} user_event[5]\n" +
			"user_event {\n" +
			"    timestamp : 21\n" +
			"} user_event[6]\n" +
			"user_event {\n" +
			"    timestamp : 30\n" +
			"} user_event[7]\n";
	private static final String frequentLogs_nonZeroBased = 
			"user_event {\n" +
			"    timestamp : 4\n" +
			"} user_event[0]\n" +
			"user_event {\n" +
			"    timestamp : 5\n" +
			"} user_event[1]\n" +
			"user_event {\n" +
			"    timestamp : 9\n" +
			"} user_event[2]\n" +
			"user_event {\n" +
			"    timestamp : 19\n" +
			"} user_event[3]\n" +
			"user_event {\n" +
			"    timestamp : 20\n" +
			"} user_event[4]\n" +
			"user_event {\n" +
			"    timestamp : 23\n" +
			"} user_event[5]\n" +
			"user_event {\n" +
			"    timestamp : 25\n" +
			"} user_event[6]\n" +
			"user_event {\n" +
			"    timestamp : 34\n" +
			"} user_event[7]\n";
	private static final List<EditorElement> canvasElementCard = Lists.newArrayList(
			new EditorElement("\"card\""), new EditorElement("\"div\""));
	private static final List<EditorElement> canvasElementInput = Lists.newArrayList(
			new EditorElement("\"card\""), new EditorElement("\"div\""));
	private static final List<EditorElement> canvasElementPage = Lists.newArrayList(
			new EditorElement("\"page\""), new EditorElement("\"div\""));
	private static final List<EditorElement> menuElementMenu = Lists.newArrayList(
			new EditorElement("\"menu\""), new EditorElement("\"div\""));
	private static final List<EditorElement> menuElementMenuButton = Lists.newArrayList(
			new EditorElement("\"menu-button\""), new EditorElement("\"div\""));
	private static final List<EditorElement> menuElementIcon = Lists.newArrayList(
			new EditorElement("\"icon\""), new EditorElement("\"div\""));
	private static final List<EditorElement> dialogBoxElementDialog = Lists.newArrayList(
			new EditorElement("\"dialog\""), new EditorElement("\"div\""));
	private static final List<EditorElement> unknownAreaElement = Lists.newArrayList(
			new EditorElement("\"div\""));
	private static final Task task = new Task();
	private static final Task task_allAreas = new Task(logsMultipleUserEvents_allAreas);
	private static final Task task_oneAreaMissing = new Task(logsMultipleUserEvents_oneAreaMissing);
	private static final Task task_twoMenus = new Task(logsMultipleUserEvents_twoMenus);
	private static final Task taskFrequentLogs_zeroBased = new Task(frequentLogs_zeroBased);
	private static final Task taskFrequentLogs_nonZeroBased = new Task(frequentLogs_nonZeroBased);
	private final UserEvent userEvent1;
	private final UserEvent userEvent2;
	private static final double EPSILON = 1e-5;
	
	public TestTask() {
		userEvent1 = new UserEvent();
		userEvent2 = new UserEvent();
		
		userEvent1.setTimestamp(0);
		userEvent2.setTimestamp(1);
		
		userEvent1.setArea(new Canvas(Lists.newArrayList(
				new EditorElement("\"input\""),
				new EditorElement("\"div\""),
				new EditorElement("\"div\""))));
		userEvent2.setArea(new Canvas(Lists.newArrayList(
				new EditorElement("\"card\""),
				new EditorElement("\"div\""))));
	}
	
	@Test
	public void meanFrequency_zeroBased() {
		double expectedMeanFrequency = 0.2;
		double computedMeanFrequency = taskFrequentLogs_zeroBased.meanFrequencyPerTenSeconds();
		
		assertEquals(expectedMeanFrequency, computedMeanFrequency, EPSILON);
	}
	
	@Test
	public void meanFrequency_nonZeroBased() {
		double expectedMeanFrequency = 0.2;
		double computedMeanFrequency = taskFrequentLogs_nonZeroBased.meanFrequencyPerTenSeconds();
		
		assertEquals(expectedMeanFrequency, computedMeanFrequency, EPSILON);
	}
	
	@Test
	public void determineAreaForCanvas_card() {
		EditorArea determinedPageArea = task.determineAreaForElements(canvasElementCard);
		
		assertTrue(determinedPageArea instanceof Canvas);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(canvasElementCard));				
	}
	
	@Test
	public void determineAreaForCanvas_input() {
		EditorArea determinedPageArea = task.determineAreaForElements(canvasElementInput);
		
		assertTrue(determinedPageArea instanceof Canvas);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(canvasElementInput));				
	}
	
	@Test
	public void determineAreaForCanvas_page() {
		EditorArea determinedPageArea = task.determineAreaForElements(canvasElementPage);
		
		assertTrue(determinedPageArea instanceof Canvas);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(canvasElementPage));				
	}
	
	@Test
	public void determineAreaForMenu_menu() {
		EditorArea determinedPageArea = task.determineAreaForElements(menuElementMenu);
		
		assertTrue(determinedPageArea instanceof Menu);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(menuElementMenu));				
	}
	
	@Test
	public void determineAreaForMenu_menuButton() {
		EditorArea determinedPageArea = task.determineAreaForElements(menuElementMenuButton);
		
		assertTrue(determinedPageArea instanceof Menu);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(menuElementMenuButton));				
	}
	
	@Test
	public void determineAreaForMenu_icon() {
		EditorArea determinedPageArea = task.determineAreaForElements(menuElementIcon);
		
		assertTrue(determinedPageArea instanceof Menu);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(menuElementIcon));				
	}
	
	@Test
	public void determineAreaForDialog() {
		EditorArea determinedPageArea = task.determineAreaForElements(dialogBoxElementDialog);
		
		assertTrue(determinedPageArea instanceof DialogBox);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(dialogBoxElementDialog));				
	}
	
	@Test
	public void determineAreaForUnknownArea() {
		EditorArea determinedPageArea = task.determineAreaForElements(unknownAreaElement);
		
		assertTrue(determinedPageArea instanceof UnknownArea);
		assertEquals(
				new HashSet(determinedPageArea.getPathInEditor()),
				new HashSet(unknownAreaElement));				
	}
	
	@Test
	public void parseLogsSingle() {
		List<UserEvent> expectedEvent = ImmutableList.of(userEvent1);
		List<UserEvent> parsedEvent = task.parseLogs(logsSingleUserEvent);
		
		assertEquals(
				new HashSet(expectedEvent),
				new HashSet(parsedEvent));
	}

	@Test
	public void parseLogsDouble() {
		List<UserEvent> expectedEvent = ImmutableList.of(userEvent1, userEvent2);
		List<UserEvent> parsedEvent = task.parseLogs(logsTwoUserEvents);
		
		assertEquals(
				new HashSet(expectedEvent),
				new HashSet(parsedEvent));
	}
	
	@Test
	public void clicksPerArea_allAreas() {
		Map<String, Double> expectedMap = Maps.newHashMap();
		expectedMap.put(Canvas.class.getCanonicalName(), (double) 1);
		expectedMap.put(Menu.class.getCanonicalName(), (double) 1);
		expectedMap.put(DialogBox.class.getCanonicalName(), (double) 1);
		expectedMap.put(UnknownArea.class.getCanonicalName(), (double) 1);
		
		Map<String, Double> computedMap = task_allAreas.computeClicksPerArea();
		
		assertEquals(
				new HashSet(expectedMap.entrySet()),
				new HashSet(computedMap.entrySet()));
	}
	
	@Test
	public void clicksPerArea_oneAreaMissing() {
		Map<String, Double> expectedMap = Maps.newHashMap();
		expectedMap.put(Canvas.class.getCanonicalName(), (double) 1);
		expectedMap.put(Menu.class.getCanonicalName(), (double) 1);
		expectedMap.put(DialogBox.class.getCanonicalName(), (double) 1);
		
		Map<String, Double> computedMap = task_oneAreaMissing.computeClicksPerArea();
		
		assertEquals(
				new HashSet(expectedMap.entrySet()),
				new HashSet(computedMap.entrySet()));
	}
	
	@Test
	public void clicksPerArea_twoMenus() {
		Map<String, Double> expectedMap = Maps.newHashMap();
		expectedMap.put(Canvas.class.getCanonicalName(), (double) 1);
		expectedMap.put(Menu.class.getCanonicalName(), (double) 2);
		expectedMap.put(DialogBox.class.getCanonicalName(), (double) 1);
		
		Map<String, Double> computedMap = task_twoMenus.computeClicksPerArea();
		
		assertEquals(
				new HashSet(expectedMap.entrySet()),
				new HashSet(computedMap.entrySet()));
	}
}
