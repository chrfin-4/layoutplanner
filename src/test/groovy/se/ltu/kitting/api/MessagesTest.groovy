package se.ltu.kitting.api;

import spock.lang.*;

public class MessagesTest extends Specification {

  def "empty messages object should not have errors or warnings"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    expect: "it does not have errors"
      !messages.hasErrors()
      !messages.hasWarnings()
  }

  def "empty messages object should not have global or part messages"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    expect: "it does not have global messages"
      !messages.hasErrors()
      !messages.hasWarnings()
    and: "it does not have global messages"
      !messages.hasGlobalMessages()
      messages.globalMessages().isEmpty()
    and: "it does not have part messages"
      !messages.hasPartMessages()
      messages.partMessages().isEmpty()
      messages.partsWithMessages().isEmpty()
      messages.partMessages(1).isEmpty()
  }

  def "should have errors after adding a global error message"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    when: "adding an info message"
      messages.add(Message.error("hello"))
    then: "it has errors"
      messages.hasErrors()
  }

  def "should have warnings after adding a global warning message"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    when: "adding an info message"
      messages.add(Message.warn("hello"))
    then: "it has warnings"
      messages.hasWarnings()
  }

  def "should have errors and warnings after adding part-specific error and warning message"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    when: "adding an info message"
      messages.add(1, Message.warn("hello"))
      messages.add(2, Message.error("hello"))
    then: "it has warnings"
      messages.hasErrors()
      messages.hasWarnings()
  }

  def "adding multiple global messages"() {
    given: "an empty messages object"
      def messages = Messages.empty()

    when: "adding an info message"
      messages.add(Message.info("hello"))
    then: "there are no errors or warnings"
      !messages.hasErrors()
      !messages.hasWarnings()

    when: "adding a global warning message"
      messages.add(Message.warn("hello"))
    then: "there are warnings but no errors"
      messages.hasWarnings()
      !messages.hasErrors()

    when: "adding another info message"
      messages.add(Message.info("hello"))
    then: "there are still warnings but no errors"
      messages.hasWarnings()
      !messages.hasErrors()

    when: "adding a global error message"
      messages.add(Message.error("hello"))
    then: "there are both warnings and errors"
      messages.hasWarnings()
      messages.hasErrors()

    when: "adding another info message"
      messages.add(Message.info("hello"))
    then: "there are still warnings and errors"
      messages.hasWarnings()
      messages.hasErrors()
  }

  def "adding multiple part-specific messages"() {
    given: "an empty messages object"
      def messages = Messages.empty()

    when: "adding an info message"
      messages.add(1, Message.info("hello"))
    then: "there are no errors or warnings"
      !messages.hasErrors()
      !messages.hasWarnings()

    when: "adding a part-specific error message"
      messages.add(2, Message.error("hello"))
    then: "there are errors but no warnings"
      !messages.hasWarnings()
      messages.hasErrors()

    when: "adding an info message to the same part"
      messages.add(2, Message.info("hello"))
    then: "there are still errors but no warnings"
      !messages.hasWarnings()
      messages.hasErrors()

    when: "adding a part-specific warning message"
      messages.add(1, Message.warn("hello"))
    then: "there are both warnings and errors"
      messages.hasWarnings()
      messages.hasErrors()

    when: "adding another info message"
      messages.add(3, Message.info("hello"))
    then: "there are still warnings and errors"
      messages.hasWarnings()
      messages.hasErrors()
  }

  def "adding and retrieving global and part-specific messages"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    and: "global and part-specific messages"
      def globalInfo = Message.info("global info")
      def globalWarning = Message.warn("global warning")
      def globalError = Message.error("global error")
      def part1Info = Message.info("part 1 info")
      def part1Warning = Message.warn("part 1 warning")
      def part2Info = Message.info("part 2 info")
      def part2Error = Message.error("part 2 error")

    when: "adding part 1 messages"
      messages.add(1, part1Info).add(1, part1Warning)
    and: "adding global messages"
      messages.add(globalInfo).add(globalWarning).add(globalError)
    and: "adding part 2 messages"
      messages.add(2, part2Info).add(2, part2Error)

    then: "there should be global messages"
      messages.hasGlobalMessages()
      messages.globalMessages().isPresent()
    and: "the list of messages should match the added messages"
      messages.globalMessages().get() == [globalInfo, globalWarning, globalError]
    and: "there should be part messages"
      messages.hasPartMessages()
    and: "the set of part IDs with messages should match the part messages"
      messages.partsWithMessages() == [1, 2] as Set
    and: "the messages for part 1 should match"
      messages.partMessages(1).get() == [part1Info, part1Warning]
    and: "the messages for part 2 should match"
      messages.partMessages(2).get() == [part2Info, part2Error]
    and: "part IDs should map to their list of messages"
      messages.partMessages() == [1: [part1Info, part1Warning], 2: [part2Info, part2Error]]
  }

  def "allMessages should return all global and part messages"() {
    given: "an empty messages object"
      def messages = Messages.empty()
      def global = [Message.info("global info"), Message.error("global error")]
      def parts = [Message.info("part info"), Message.warn("part warning")]
    when: "messages are added"
      messages.add(global[0]).add(global[1]).add(0, parts[0]).add(1, parts[1])
    then: "allMessages should return all messages"
      messages.allMessages() as Set == global + parts as Set
  }

  def "messages should be unchange when merging in a null object"() {
    given: "an empty messages object"
      def messages = Messages.empty()
    when: "messages are added"
      messages.add(Message.info("hello")).add(1, Message.warn("hello"))
      def all = messages.allMessages() as Set
    and: "messages are merged with null"
      messages.merge(null)
    then: "the messages are left unchanged"
      messages.allMessages() as Set == all
  }

  def "merging messages"() {
    given: "three empty messages object"
      def messages1 = Messages.empty()
      def messages2 = Messages.empty()
      def messages3 = Messages.empty()
    and: "global and part-specific messages"
      def globalInfo = Message.info("global info")
      def globalWarning = Message.warn("global warning")
      def globalError = Message.error("global error")
      def part1Info = Message.info("part 1 info")
      def part1Warning = Message.warn("part 1 warning")
      def part2Info = Message.info("part 2 info")
      def part3Error = Message.error("part 3 error")

    when: "adding the info messages to messages 1"
      messages1.add(globalInfo).add(globalWarning)      // global, info + warning
      def all1 = messages1.allMessages() as Set

    and: "adding the warning messages to messages 2"
      messages2.add(1, part1Info).add(3, part3Error)    // part, info + error
      def all2 = messages2.allMessages() as Set

    and: "adding the error messages to messages 3"
      messages3.add(globalError).add(1, part1Warning).add(2, part2Info)     // gobal + part, info + warning + error
      def all3 = messages3.allMessages() as Set

    then: "messages 1 should have warnings but no errors"
      !messages1.hasErrors()
      messages1.hasWarnings()
    and: "should have gobal but no part messages"
      messages1.hasGlobalMessages()
      !messages1.hasPartMessages()

    and: "messages 2 should have errors but no warnings"
      messages2.hasErrors()
      !messages2.hasWarnings()
    and: "should have part messages but no global messages"
      !messages2.hasGlobalMessages()
      messages2.hasPartMessages()

    and: "messages 3 should have errors and warnings"
      messages3.hasErrors()
      messages3.hasWarnings()
    and: "should have gobal and part messages"
      messages3.hasGlobalMessages()
      messages3.hasPartMessages()

    when: "messages 2 is merged into messages 1"
      messages1.merge(messages2)

    then: "messages 1 should also have warnings and errors"
      messages1.hasErrors()
      messages1.hasWarnings()
    and: "should have gobal and part messages"
      messages1.hasGlobalMessages()
      messages1.hasPartMessages()
    and: "the messages in 1 should be those from 1 and from 2"
      messages1.allMessages() as Set == all1 + all2

    when: "messages 3 is merged into 1"
      messages1.merge(messages3)
    then: "the messages in 1 should be those from 1, 2, and 3"
      messages1.allMessages() as Set == all1 + all2 + all3
    and: "messages 2 and 3 should remain unchanged"
      messages2.allMessages() as Set == all2
      messages3.allMessages() as Set == all3

  }

}
