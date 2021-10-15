package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.storage.JsonAdaptedGroup.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.group.Group;
import seedu.address.model.names.Name;
import seedu.address.model.person.Person;

public class JsonAdaptedGroupTest {

    @Test
    public void toModelType_validGroupDetails_returnsGroup() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("0-1");
        groupMateIds.add("1-2");
        groupMateIds.add("3-5");
        groupMateIds.add("8-d");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), CARL);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();

        Name groupName = new Name("group");
        Group group = new Group(groupName);
        group.add(ALICE);
        group.add(BENSON);
        group.add(CARL);
        group.add(DANIEL);

        assertEquals(group, jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        String groupNameString = null;
        List<String> groupMateIds = new ArrayList<>();
        Map<Id, Person> idToPersonMap = new HashMap<>();
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        String groupNameString = "T3@m";
        List<String> groupMateIds = new ArrayList<>();
        Map<Id, Person> idToPersonMap = new HashMap<>();
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_nullGroupMateIds_returnsGroupWithNoGroupMates() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = null;
        Map<Id, Person> idToPersonMap = new HashMap<>();
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();

        Name groupName = new Name("group");
        Group group = new Group(groupName);

        assertEquals(group, jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_emptyGroupMateIds_returnsGroupWithNoGroupMates() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        Map<Id, Person> idToPersonMap = new HashMap<>();
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();

        Name groupName = new Name("group");
        Group group = new Group(groupName);

        assertEquals(group, jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_invalidGroupMateIdFormat_throwsIllegalValueException() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("0-1");
        groupMateIds.add("g-2");
        groupMateIds.add("3-5");
        groupMateIds.add("8-d");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), CARL);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = Id.MESSAGE_MALFORMED_ID;
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_missingGroupMateId_throwsIllegalValueException() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("0-1");
        groupMateIds.add("1-2");
        groupMateIds.add("3-5");
        groupMateIds.add("8-d");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = JsonAdaptedGroup.MESSAGE_NO_SUCH_PERSON;
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_duplicateGroupMateId_throwsIllegalValueException() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("0-1");
        groupMateIds.add("1-2");
        groupMateIds.add("3-5");
        groupMateIds.add("0-1");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), CARL);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = JsonAdaptedGroup.MESSAGE_DUPLICATE_GROUP_MATE;
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_differentGroupMateIdReferencingSameGroupMate_throwsIllegalValueException()
            throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("0-1");
        groupMateIds.add("1-2");
        groupMateIds.add("3-5");
        groupMateIds.add("8-d");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), BENSON);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();
        String expectedMessage = JsonAdaptedGroup.MESSAGE_DUPLICATE_GROUP_MATE;
        assertThrows(IllegalValueException.class, expectedMessage, () -> jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_groupMateOrdering_returnsGroupWithCorrectGroupMateOrdering() throws IllegalValueException {
        String groupNameString = "group";
        List<String> groupMateIds = new ArrayList<>();
        groupMateIds.add("8-d");
        groupMateIds.add("3-5");
        groupMateIds.add("1-2");
        groupMateIds.add("0-1");
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), CARL);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        JsonAdaptedGroup jsonAdaptedGroup = new JsonAdaptedGroup.Builder(groupNameString, groupMateIds).build();

        Name groupName = new Name("group");
        Group group = new Group(groupName);
        group.add(ALICE);
        group.add(BENSON);
        group.add(CARL);
        group.add(DANIEL);

        assertNotEquals(group, jsonAdaptedGroup.toModelType(idToPersonMap));

        groupName = new Name("group");
        group = new Group(groupName);
        group.add(DANIEL);
        group.add(CARL);
        group.add(BENSON);
        group.add(ALICE);

        assertEquals(group, jsonAdaptedGroup.toModelType(idToPersonMap));
    }

    @Test
    public void toModelType_respectiveGroupMatesOnly_returnsGroupWithOnlyRespectiveGroupMates()
            throws IllegalValueException {
        Map<Id, Person> idToPersonMap = new HashMap<>();
        idToPersonMap.put(Id.parse("0-1"), ALICE);
        idToPersonMap.put(Id.parse("1-2"), BENSON);
        idToPersonMap.put(Id.parse("3-5"), CARL);
        idToPersonMap.put(Id.parse("8-d"), DANIEL);
        idToPersonMap.put(Id.parse("15-22"), ELLE);
        idToPersonMap.put(Id.parse("37-59"), FIONA);
        idToPersonMap.put(Id.parse("90-e9"), GEORGE);
        idToPersonMap.put(Id.parse("179-262"), HOON);
        idToPersonMap.put(Id.parse("3db-63d"), IDA);

        String group1NameString = "group1";
        List<String> group1MateIds = new ArrayList<>();
        group1MateIds.add("0-1");
        group1MateIds.add("3-5");
        group1MateIds.add("8-d");
        group1MateIds.add("37-59");
        JsonAdaptedGroup jsonAdaptedGroup1 = new JsonAdaptedGroup.Builder(group1NameString, group1MateIds).build();

        Name group1Name = new Name("group1");
        Group group1 = new Group(group1Name);
        group1.add(ALICE);
        group1.add(CARL);
        group1.add(DANIEL);
        group1.add(FIONA);

        String group2NameString = "group2";
        List<String> group2MateIds = new ArrayList<>();
        group2MateIds.add("1-2");
        group2MateIds.add("179-262");
        group2MateIds.add("3-5");
        group2MateIds.add("37-59");
        group2MateIds.add("15-22");
        JsonAdaptedGroup jsonAdaptedGroup2 = new JsonAdaptedGroup.Builder(group2NameString, group2MateIds).build();

        Name group2Name = new Name("group2");
        Group group2 = new Group(group2Name);
        group2.add(BENSON);
        group2.add(HOON);
        group2.add(CARL);
        group2.add(FIONA);
        group2.add(ELLE);

        String group3NameString = "group3";
        List<String> group3MateIds = new ArrayList<>();
        group3MateIds.add("3db-63d");
        group3MateIds.add("179-262");
        group3MateIds.add("0-1");
        JsonAdaptedGroup jsonAdaptedGroup3 = new JsonAdaptedGroup.Builder(group3NameString, group3MateIds).build();

        Name group3Name = new Name("group3");
        Group group3 = new Group(group3Name);
        group3.add(IDA);
        group3.add(HOON);
        group3.add(ALICE);

        assertEquals(group1, jsonAdaptedGroup1.toModelType(idToPersonMap));
        assertEquals(group2, jsonAdaptedGroup2.toModelType(idToPersonMap));
        assertEquals(group3, jsonAdaptedGroup3.toModelType(idToPersonMap));
    }
}
