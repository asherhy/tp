package seedu.address.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Stack;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.StateDependentCommand;
import seedu.address.logic.commands.UndoableCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.state.ApplicationState;
import seedu.address.logic.state.HomeState;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.group.Group;
import seedu.address.model.person.Person;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_COMMAND_EXECUTION_IN_INVALID_APP_STATE = "This command cannot be run here.";
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private ApplicationState currentApplicationState;
    private Stack<UndoableCommand> undoableCommandStack;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
        currentApplicationState = new HomeState();
        undoableCommandStack = new Stack<>();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText, currentApplicationState);
        checkIfCommandCanRunInApplicationState(command);
        commandResult = command.execute(model);
        processCommandResult(commandResult);
        commandResult = undoIfMustUndo(commandResult);
        addToUndoableCommandStackIfUndoable(command);

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (IOException ioe) {
            throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
        }

        return commandResult;
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public ObservableList<Group> getFilteredGroupList() {
        return model.getFilteredGroupList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    private void checkIfCommandCanRunInApplicationState(Command command) throws CommandException {
        boolean isAbleToRunInApplicationState = !(command instanceof StateDependentCommand)
                || ((StateDependentCommand) command).isAbleToRunInApplicationState(currentApplicationState);
        if (!isAbleToRunInApplicationState) {
            throw new CommandException(MESSAGE_COMMAND_EXECUTION_IN_INVALID_APP_STATE);
        }
    }

    private void processCommandResult(CommandResult commandResult) {
        currentApplicationState = commandResult.getNextApplicationState();
    }

    private CommandResult undoIfMustUndo(CommandResult commandResult) throws CommandException {
        boolean mustUndo = commandResult.isGoingToCauseUndo();
        boolean canUndo = !undoableCommandStack.empty();
        if (!(mustUndo && canUndo)) {
            return commandResult;
        }
        UndoableCommand undoableCommand = undoableCommandStack.pop();
        CommandResult undoResult = undoableCommand.undo(model);
        processCommandResult(undoResult);
        return undoResult;
    }

    private void addToUndoableCommandStackIfUndoable(Command command) {
        if (command instanceof UndoableCommand) {
            undoableCommandStack.push((UndoableCommand) command);
        }
    }
}
