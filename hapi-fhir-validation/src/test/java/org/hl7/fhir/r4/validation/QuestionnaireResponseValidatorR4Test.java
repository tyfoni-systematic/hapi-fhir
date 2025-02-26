package org.hl7.fhir.r4.validation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.util.TestUtil;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeSystem;
import org.hl7.fhir.r4.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.MarkdownType;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.UriType;
import org.hl7.fhir.r4.model.ValueSet;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireResponseValidatorR4Test {
	public static final String ID_ICC_QUESTIONNAIRE_SETUP = "http://example.com/Questionnaire/profile";
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(QuestionnaireResponseValidatorR4Test.class);
	private static final String CODE_ICC_SCHOOLTYPE_PT = "PT";
	private static final String ID_VS_SCHOOLTYPE = "ValueSet/schooltype";
	private static final String SYSTEMURI_ICC_SCHOOLTYPE = "http://ehealthinnovation/icc/ns/schooltype";
	private static final FhirContext ourCtx = FhirContext.forR4();
	private static DefaultProfileValidationSupport myDefaultValidationSupport = new DefaultProfileValidationSupport(ourCtx);
	private FhirInstanceValidator myInstanceVal;
	private FhirValidator myVal;
	@Mock(strictness = Mock.Strictness.LENIENT)
	private IValidationSupport myValSupport;

	@BeforeEach
	public void before() {
		when(myValSupport.getFhirContext()).thenReturn(ourCtx);

		myVal = ourCtx.newValidator();
		myVal.setValidateAgainstStandardSchema(false);
		myVal.setValidateAgainstStandardSchematron(false);

		ValidationSupportChain validationSupport = new ValidationSupportChain(myDefaultValidationSupport, myValSupport, new InMemoryTerminologyServerValidationSupport(ourCtx), new CommonCodeSystemsTerminologyService(ourCtx));
		myInstanceVal = new FhirInstanceValidator(validationSupport);

		myVal.registerValidatorModule(myInstanceVal);

	}

	private ValidationResult stripBindingHasNoSourceMessage(ValidationResult theErrors) {
		List<SingleValidationMessage> messages = new ArrayList<>(theErrors.getMessages());
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).getMessage().contains("has no source, so can't")) {
				messages.remove(i);
				i--;
			}
		}

		return new ValidationResult(ourCtx, messages);
	}

	@Test
	public void testAnswerWithCorrectType() {
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl("http://codesystems.com/system");
		codeSystem.addConcept().setCode("code0");
		when(myValSupport.fetchCodeSystem(eq("http://codesystems.com/system"))).thenReturn(codeSystem);

		ValueSet options = new ValueSet();
		options.getCompose().addInclude().setSystem("http://codesystems.com/system").addConcept().setCode("code0");
		when(myValSupport.fetchValueSet(eq("http://somevalueset"))).thenReturn(options);

		int itemCnt = 16;
		QuestionnaireItemType[] questionnaireItemTypes = new QuestionnaireItemType[itemCnt];
		questionnaireItemTypes[0] = QuestionnaireItemType.BOOLEAN;
		questionnaireItemTypes[1] = QuestionnaireItemType.DECIMAL;
		questionnaireItemTypes[2] = QuestionnaireItemType.INTEGER;
		questionnaireItemTypes[3] = QuestionnaireItemType.DATE;
		questionnaireItemTypes[4] = QuestionnaireItemType.DATETIME;
		questionnaireItemTypes[5] = QuestionnaireItemType.TIME;
		questionnaireItemTypes[6] = QuestionnaireItemType.STRING;
		questionnaireItemTypes[7] = QuestionnaireItemType.TEXT;
		questionnaireItemTypes[8] = QuestionnaireItemType.TEXT;
		questionnaireItemTypes[9] = QuestionnaireItemType.URL;
		questionnaireItemTypes[10] = QuestionnaireItemType.CHOICE;
		questionnaireItemTypes[11] = QuestionnaireItemType.OPENCHOICE;
		questionnaireItemTypes[12] = QuestionnaireItemType.OPENCHOICE;
		questionnaireItemTypes[13] = QuestionnaireItemType.ATTACHMENT;
		questionnaireItemTypes[14] = QuestionnaireItemType.REFERENCE;
		questionnaireItemTypes[15] = QuestionnaireItemType.QUANTITY;

		Type[] answerValues = new Type[itemCnt];
		answerValues[0] = new BooleanType(true);
		answerValues[1] = new DecimalType(42.0);
		answerValues[2] = new IntegerType(42);
		answerValues[3] = new DateType(new Date());
		answerValues[4] = new DateTimeType(new Date());
		answerValues[5] = new TimeType("04:47:12");
		answerValues[6] = new StringType("some text");
		answerValues[7] = new StringType("some text");
		answerValues[8] = new MarkdownType("some text");
		answerValues[9] = new UriType("http://example.com");
		answerValues[10] = new Coding().setSystem("http://codesystems.com/system").setCode("code0");
		answerValues[11] = new Coding().setSystem("http://codesystems.com/system").setCode("code0");
		answerValues[12] = new StringType("some value");
		answerValues[13] = new Attachment().setData("some data".getBytes()).setContentType("txt");
		answerValues[14] = new Reference("http://example.com/Questionnaire/q1");
		answerValues[15] = new Quantity(42);


		for (int i = 0; i < itemCnt; i++) {
			ourLog.info("Testing item {}: {}", i, questionnaireItemTypes[i]);

			Questionnaire q = new Questionnaire();
			if (questionnaireItemTypes[i] == null) continue;
			String linkId = "link" + i;
			QuestionnaireItemComponent questionnaireItemComponent =
				q.addItem().setLinkId(linkId).setRequired(true).setType(questionnaireItemTypes[i]);
			if (i == 10 || i == 11) {
				questionnaireItemComponent.setAnswerValueSet("http://somevalueset");
			} else if (i == 12) {
				questionnaireItemComponent.setAnswerOption(
					Arrays.asList(new Questionnaire.QuestionnaireItemAnswerOptionComponent(new StringType("some value"))));
			}

			QuestionnaireResponse qa = new QuestionnaireResponse();
			qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
			qa.setStatus(QuestionnaireResponseStatus.INPROGRESS);
			qa.setQuestionnaire("http://example.com/Questionnaire/q" + i);
			qa.addItem().setLinkId(linkId).addAnswer().setValue(answerValues[i]);

			when(myValSupport.fetchResource(eq(Questionnaire.class),
				eq(qa.getQuestionnaire()))).thenReturn(q);
			when(myValSupport.validateCode(any(), any(), any(), any(), any(), nullable(String.class)))
				.thenReturn(new IValidationSupport.CodeValidationResult().setSeverity(IValidationSupport.IssueSeverity.ERROR).setMessage("Unknown code"));
			when(myValSupport.validateCodeInValueSet(any(), any(), any(), any(), any(), nullable(ValueSet.class)))
				.thenReturn(new IValidationSupport.CodeValidationResult().setCode("code0"));
			when(myValSupport.validateCode(any(), any(), any(), any(), any(), nullable(String.class)))
				.thenReturn(new IValidationSupport.CodeValidationResult().setCode("code0"));


			ValidationResult errors = myVal.validateWithResult(qa);

			ourLog.info(errors.toString());
			assertThat(errors.getMessages()).as("index[" + i + "]: " + errors).isEmpty();
		}
	}

	@Test
	public void testAnswerWithWrongType() {
		Questionnaire q = new Questionnaire();
		q.addItem().setLinkId("link0").setRequired(true).setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		qa.addItem().setLinkId("link0").addAnswer().setValue(new StringType("FOO"));

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaireElement().getValue()))).thenReturn(q);

		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("Answer value must be of the type boolean");
	}

	@Test
	public void testCodedAnswer() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";

		Questionnaire q = new Questionnaire();
		q.addItem().setLinkId("link0").setRequired(false).setType(QuestionnaireItemType.CHOICE).setAnswerValueSet("http://somevalueset");
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq("http://example.com/Questionnaire/q1"))).thenReturn(q);

		when(myValSupport.isCodeSystemSupported(any(), eq("http://codesystems.com/system"))).thenReturn(true);
		when(myValSupport.isCodeSystemSupported(any(), eq("http://codesystems.com/system2"))).thenReturn(true);
		when(myValSupport.validateCode(any(), any(), eq("http://codesystems.com/system"), eq("code0"), any(), nullable(String.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setCode("code0"));
		when(myValSupport.validateCode(any(), any(), eq("http://codesystems.com/system"), eq("code1"), any(), nullable(String.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setSeverity(IValidationSupport.IssueSeverity.ERROR).setMessage("Unknown code"));
		when(myValSupport.validateCodeInValueSet(any(), any(), eq("http://codesystems.com/system"), eq("code0"), any(), nullable(ValueSet.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setCode("code0"));
		when(myValSupport.validateCodeInValueSet(any(), any(), eq("http://codesystems.com/system"), eq("code1"), any(), nullable(ValueSet.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setSeverity(IValidationSupport.IssueSeverity.ERROR).setMessage("Unknown code"));

		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl("http://codesystems.com/system");
		codeSystem.addConcept().setCode("code0");
		when(myValSupport.fetchCodeSystem(eq("http://codesystems.com/system"))).thenReturn(codeSystem);

		CodeSystem codeSystem2 = new CodeSystem();
		codeSystem2.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem2.setUrl("http://codesystems.com/system2");
		codeSystem2.addConcept().setCode("code2");
		when(myValSupport.fetchCodeSystem(eq("http://codesystems.com/system2"))).thenReturn(codeSystem2);

		ValueSet options = new ValueSet();
		options.getCompose().addInclude().setSystem("http://codesystems.com/system").addConcept().setCode("code0");
		options.getCompose().addInclude().setSystem("http://codesystems.com/system2").addConcept().setCode("code2");
		when(myValSupport.fetchValueSet(eq("http://somevalueset"))).thenReturn(options);

		QuestionnaireResponse qa;
		ValidationResult errors;

		// Good code

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system").setCode("code0"));
		errors = myVal.validateWithResult(qa);
		errors = stripBindingHasNoSourceMessage(errors);
		assertThat(errors.getMessages().size()).as(errors.toString()).isEqualTo(0);

		// Bad code

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system").setCode("code1"));
		errors = myVal.validateWithResult(qa);
		errors = stripBindingHasNoSourceMessage(errors);
		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("Unknown code (for 'http://codesystems.com/system#code1')");
		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]");

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system2").setCode("code3"));
		errors = myVal.validateWithResult(qa);
		errors = stripBindingHasNoSourceMessage(errors);
		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("Unknown code 'http://codesystems.com/system2#code3'");
		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]");

	}

	@Test
	public void testGroupWithNoLinkIdInQuestionnaireResponse() {
		Questionnaire q = new Questionnaire();
		QuestionnaireItemComponent qGroup = q.addItem().setType(QuestionnaireItemType.GROUP);
		qGroup.addItem().setLinkId("link0").setRequired(true).setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		QuestionnaireResponseItemComponent qaGroup = qa.addItem();
		qaGroup.addItem().setLinkId("link0").addAnswer().setValue(new StringType("FOO"));

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaire()))).thenReturn(q);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("QuestionnaireResponse.item.linkId: minimum required = 1, but only found 0");
	}

	@Test
	public void testItemWithNoType() {
		Questionnaire q = new Questionnaire();
		QuestionnaireItemComponent qItem = q.addItem();
		qItem.setLinkId("link0");

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		QuestionnaireResponseItemComponent qaItem = qa.addItem().setLinkId("link0");
		qaItem.addAnswer().setValue(new StringType("FOO"));

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaire()))).thenReturn(q);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("Definition for item link0 does not contain a type");
		assertThat(errors.getMessages()).hasSize(1);
	}

	@Test
	public void testMissingRequiredQuestion() {

		Questionnaire q = new Questionnaire();
		q.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		q.addItem().setLinkId("link0").setRequired(true).setType(QuestionnaireItemType.STRING);
		q.addItem().setLinkId("link1").setRequired(true).setType(QuestionnaireItemType.STRING);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);

		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		qa.addItem().setLinkId("link1").addAnswer().setValue(new StringType("FOO"));

		String reference = qa.getQuestionnaire();
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(reference))).thenReturn(q);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("No response answer found for required item 'link0'");
	}

	@Test
	public void testEmbeddedItemInChoice() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";
		String valueSetRef = "http://somevalueset";
		String codeSystemUrl = "http://codesystems.com/system";
		String codeValue = "code0";

		// create the questionnaire
		QuestionnaireItemComponent item1 = new QuestionnaireItemComponent();
		item1.setLinkId("link1")
			.setType(QuestionnaireItemType.CHOICE)
			.setAnswerValueSet(valueSetRef);

		item1.addItem().setLinkId("link11")
			.setType(QuestionnaireItemType.TEXT);

		Questionnaire q = new Questionnaire();
		q.addItem(item1);
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(questionnaireRef)))
			.thenReturn(q);

		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl(codeSystemUrl);
		codeSystem.addConcept().setCode(codeValue);
		when(myValSupport.fetchCodeSystem(eq(codeSystemUrl)))
			.thenReturn(codeSystem);

		ValueSet options = new ValueSet();
		options.getCompose().addInclude().setSystem(codeSystemUrl).addConcept().setCode(codeValue);
		when(myValSupport.fetchValueSet(eq(valueSetRef)))
			.thenReturn(options);
		when(myValSupport.validateCode(any(), any(), eq(codeSystemUrl), eq(codeValue), any(String.class), anyString()))
			.thenReturn(new IValidationSupport.CodeValidationResult().setCode(codeValue));

		IParser xmlParser = ourCtx.newXmlParser().setPrettyPrint(true);
		String qXml = xmlParser.encodeResourceToString(q);
		ourLog.info(qXml);

		// create the response
		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.INPROGRESS);
		qa.setQuestionnaire(questionnaireRef);
		qa.addItem().setLinkId("link1")
			.addAnswer()
			.addItem().setLinkId("link11");

		String rXml = xmlParser.encodeResourceToString(qa);
		ourLog.info(rXml);

		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.getMessages()).isEmpty();
	}

	@Test
	public void testEmbeddedItemInOpenChoice() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";
		String valueSetRef = "http://somevalueset";
		String codeSystemUrl = "http://codesystems.com/system";
		String codeValue = "code0";

		// create the questionnaire
		QuestionnaireItemComponent item1 = new QuestionnaireItemComponent();
		item1.setLinkId("link1")
			.setType(QuestionnaireItemType.OPENCHOICE)
			.setAnswerValueSet(valueSetRef);

		item1.addItem().setLinkId("link11")
			.setType(QuestionnaireItemType.TEXT);

		Questionnaire q = new Questionnaire();
		q.addItem(item1);
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(questionnaireRef)))
			.thenReturn(q);

		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl(codeSystemUrl);
		codeSystem.addConcept().setCode(codeValue);
		when(myValSupport.fetchCodeSystem(eq(codeSystemUrl)))
			.thenReturn(codeSystem);

		ValueSet options = new ValueSet();
		options.getCompose().addInclude().setSystem(codeSystemUrl).addConcept().setCode(codeValue);
		when(myValSupport.fetchValueSet(eq(valueSetRef)))
			.thenReturn(options);
		when(myValSupport.validateCode(any(), any(), eq(codeSystemUrl), eq(codeValue), any(String.class), anyString()))
			.thenReturn(new IValidationSupport.CodeValidationResult().setCode(codeValue));

		IParser xmlParser = ourCtx.newXmlParser().setPrettyPrint(true);
		String qXml = xmlParser.encodeResourceToString(q);
		ourLog.info(qXml);

		// create the response
		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.INPROGRESS);
		qa.setQuestionnaire(questionnaireRef);
		qa.addItem().setLinkId("link1")
			.addAnswer()
			.addItem().setLinkId("link11");

		String rXml = xmlParser.encodeResourceToString(qa);
		ourLog.info(rXml);

		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.getMessages()).isEmpty();
	}

	@Test
	public void testEmbeddedItemInString() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";

		// create the questionnaire
		QuestionnaireItemComponent item1 = new QuestionnaireItemComponent();
		item1.setLinkId("link1")
			.setType(QuestionnaireItemType.TEXT);

		item1.addItem().setLinkId("link11")
			.setType(QuestionnaireItemType.TEXT);

		Questionnaire q = new Questionnaire();
		q.addItem(item1);
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(questionnaireRef)))
			.thenReturn(q);

		IParser xmlParser = ourCtx.newXmlParser().setPrettyPrint(true);
		String qXml = xmlParser.encodeResourceToString(q);
		ourLog.info(qXml);

		// create the response
		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.INPROGRESS);
		qa.setQuestionnaire(questionnaireRef);
		qa.addItem().setLinkId("link1")
			.addAnswer()
			.addItem().setLinkId("link11");

		String rXml = xmlParser.encodeResourceToString(qa);
		ourLog.info(rXml);

		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.getMessages()).isEmpty();
	}

	@Test
	public void testMissingRequiredAnswer() {
		Questionnaire q = new Questionnaire();
		q.addItem().setLinkId("link0")
			.setType(QuestionnaireItemType.STRING)
			.setRequired(true);

		String reference = "http://example.com/Questionnaire/q1";
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(reference)))
			.thenReturn(q);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setQuestionnaire(reference);
		qa.addItem().setLinkId("link0");
		qa.setStatus(QuestionnaireResponseStatus.INPROGRESS);

		ValidationResult errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		assertThat(errors.getMessages()).hasSize(1);
		assertEquals(ResultSeverityEnum.WARNING, errors.getMessages().get(0).getSeverity());

		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);

		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		assertThat(errors.getMessages()).hasSize(1);
		assertEquals(ResultSeverityEnum.ERROR, errors.getMessages().get(0).getSeverity());
	}

	@Test
	public void testOpenchoiceAnswer() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";

		Questionnaire q = new Questionnaire();
		QuestionnaireItemComponent item = q.addItem();
		item.setLinkId("link0").setRequired(true).setType(QuestionnaireItemType.OPENCHOICE).setAnswerValueSet("http://somevalueset");
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(questionnaireRef))).thenReturn(q);

		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl("http://codesystems.com/system");
		codeSystem.addConcept().setCode("code0");
		when(myValSupport.fetchCodeSystem(eq("http://codesystems.com/system"))).thenReturn(codeSystem);

		CodeSystem codeSystem2 = new CodeSystem();
		codeSystem2.setContent(CodeSystemContentMode.COMPLETE);
		codeSystem2.setUrl("http://codesystems.com/system2");
		codeSystem2.addConcept().setCode("code2");
		when(myValSupport.fetchCodeSystem(eq("http://codesystems.com/system2"))).thenReturn(codeSystem2);

		ValueSet options = new ValueSet();
		options.getCompose().addInclude().setSystem("http://codesystems.com/system").addConcept().setCode("code0");
		options.getCompose().addInclude().setSystem("http://codesystems.com/system2").addConcept().setCode("code2");
		when(myValSupport.fetchValueSet(eq("http://somevalueset"))).thenReturn(options);
		when(myValSupport.validateCode(any(), any(), eq("http://codesystems.com/system"), eq("code0"), any(), nullable(String.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setCode("code0"));
		when(myValSupport.validateCode(any(), any(), eq("http://codesystems.com/system"), eq("code1"), any(), nullable(String.class)))
			.thenReturn(new IValidationSupport.CodeValidationResult().setSeverity(IValidationSupport.IssueSeverity.ERROR).setCode("Unknown code"));

		QuestionnaireResponse qa;
		ValidationResult errors;

//		// Good code
//
//		qa = new QuestionnaireResponse();
//		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
//		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
//		qa.getQuestionnaireElement().setValue(questionnaireRef);
//		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system").setCode("code0"));
//		errors = myVal.validateWithResult(qa);
//		errors = stripBindingHasNoSourceMessage(errors);
//		assertEquals(errors.getMessages().toString(), 0, errors.getMessages().size());
//
//		// Bad code
//
//		qa = new QuestionnaireResponse();
//		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
//		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
//		qa.getQuestionnaireElement().setValue(questionnaireRef);
//		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system").setCode("code1"));
//		errors = myVal.validateWithResult(qa);
//		errors = stripBindingHasNoSourceMessage(errors);
//		ourLog.info(errors.toString());
//		assertThat(errors.toString()).contains("The value provided (http://codesystems.com/system::code1) is not in the options value set in the questionnaire"));
//		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]"));
//
//		// Partial code
//
//		qa = new QuestionnaireResponse();
//		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
//		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
//		qa.getQuestionnaireElement().setValue(questionnaireRef);
//		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem(null).setCode("code1"));
//		errors = myVal.validateWithResult(qa);
//		errors = stripBindingHasNoSourceMessage(errors);
//		ourLog.info(errors.toString());
//		assertThat(errors.toString()).contains("The value provided (null::code1) is not in the options value set in the questionnaire"));
//		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]"));
//
//		qa = new QuestionnaireResponse();
//		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
//		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
//		qa.getQuestionnaireElement().setValue(questionnaireRef);
//		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("").setCode("code1"));
//		errors = myVal.validateWithResult(qa);
//		errors = stripBindingHasNoSourceMessage(errors);
//		ourLog.info(errors.toString());
//		assertThat(errors.toString()).contains("The value provided (null::code1) is not in the options value set in the questionnaire"));
//		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]"));
//
//		// System only in known codesystem
//		qa = new QuestionnaireResponse();
//		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
//		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
//		qa.getQuestionnaireElement().setValue(questionnaireRef);
//		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://codesystems.com/system").setCode(null));
//		errors = myVal.validateWithResult(qa);
//		ourLog.info(errors.toString());
//		assertThat(errors.toString()).contains("The value provided (http://codesystems.com/system::null) is not in the options value set in the questionnaire"));
//		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]"));

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://system").setCode(null));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		// This is set in InstanceValidator#validateAnswerCode
		assertThat(errors.toString()).contains("ValidationResult{messageCount=1, isSuccessful=false, description='");
		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]");

		// Wrong type

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new IntegerType(123));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());

		assertThat(errors.toString()).contains("Cannot validate integer answer option because no option list is provided");
		assertThat(errors.toString()).contains("QuestionnaireResponse.item[0].answer[0]");

		// String answer

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new StringType("Hello"));
		errors = myVal.validateWithResult(qa);
		List<SingleValidationMessage> warningsAndErrors = errors
			.getMessages()
			.stream()
			.filter(t -> t.getSeverity().ordinal() > ResultSeverityEnum.INFORMATION.ordinal())
			.collect(Collectors.toList());
		assertThat(warningsAndErrors).isEmpty();

		// Missing String answer

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setDisplay(""));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("No response answer found for required item 'link0'");

	}


	@Test
	public void testOpenchoiceAnswerWithOptions() {
		String questionnaireRef = "http://example.com/Questionnaire/q1";

		List<Questionnaire.QuestionnaireItemAnswerOptionComponent> options = new ArrayList<>();
		options.add(new Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(new Coding("http://foo", "foo", "FOOOO")));
		options.add(new Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(new Coding("http://bar", "bar", "FOOOO")));
		options.add(new Questionnaire.QuestionnaireItemAnswerOptionComponent().setValue(new StringType("Hello")));

		Questionnaire q = new Questionnaire();
		QuestionnaireItemComponent item = q.addItem();
		item.setLinkId("link0")
			.setRequired(true)
			.setType(QuestionnaireItemType.OPENCHOICE)
			.setAnswerOption(options);
		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(questionnaireRef))).thenReturn(q);

		QuestionnaireResponse qa;
		ValidationResult errors;

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://foo").setCode("foo"));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		assertEquals(true, errors.isSuccessful());

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new StringType("Hello"));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		List<SingleValidationMessage> warningsAndErrors = errors
			.getMessages()
			.stream()
			.filter(t -> t.getSeverity().ordinal() > ResultSeverityEnum.INFORMATION.ordinal())
			.collect(Collectors.toList());
		assertThat(warningsAndErrors).isEmpty();

		qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue(questionnaireRef);
		qa.addItem().setLinkId("link0").addAnswer().setValue(new Coding().setSystem("http://foo").setCode("hello"));
		errors = myVal.validateWithResult(qa);
		ourLog.info(errors.toString());
		// This is set in InstanceValidator#validateAnswerCode
		assertEquals(false, errors.isSuccessful());

	}


	@Test
	public void testUnexpectedAnswer() {
		Questionnaire q = new Questionnaire();
		q.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		q.addItem().setLinkId("link0").setRequired(false).setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);

		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		qa.addItem().setLinkId("link1").addAnswer().setValue(new StringType("FOO"));

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaire()))).thenReturn(q);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains(" - QuestionnaireResponse");
		assertThat(errors.toString()).contains("LinkId 'link1' not found in questionnaire");
	}

	@Test
	public void testUnexpectedGroup() {
		Questionnaire q = new Questionnaire();
		q.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);

		q.addItem().setLinkId("link0").setRequired(false).setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);

		qa.setStatus(QuestionnaireResponseStatus.COMPLETED);
		qa.getQuestionnaireElement().setValue("http://example.com/Questionnaire/q1");
		qa.addItem().setLinkId("link1").addItem().setLinkId("link2");

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaire()))).thenReturn(q);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains(" - QuestionnaireResponse");
		assertThat(errors.toString()).contains("LinkId 'link1' not found in questionnaire");
	}

	@Test
	public void testValidateQuestionnaireResponseWithValueSetChoiceAnswer() {
		/*
		 * Create CodeSystem
		 */
		CodeSystem codeSystem = new CodeSystem();
		codeSystem.setContent(CodeSystem.CodeSystemContentMode.COMPLETE);
		codeSystem.setUrl(SYSTEMURI_ICC_SCHOOLTYPE);
		codeSystem.addConcept().setCode(CODE_ICC_SCHOOLTYPE_PT);

		/*
		 * Create valueset
		 */
		ValueSet iccSchoolTypeVs = new ValueSet();
		iccSchoolTypeVs.setId(ID_VS_SCHOOLTYPE);
		iccSchoolTypeVs.getCompose().getIncludeFirstRep().setSystem(SYSTEMURI_ICC_SCHOOLTYPE);
		iccSchoolTypeVs
			.getCompose()
			.getIncludeFirstRep()
			.addConcept()
			.setCode(CODE_ICC_SCHOOLTYPE_PT)
			.setDisplay("Part Time");

		/*
		 * Create Questionnaire
		 */
		Questionnaire questionnaire = new Questionnaire();
		{
			questionnaire.setId(ID_ICC_QUESTIONNAIRE_SETUP);

			Questionnaire.QuestionnaireItemComponent basicGroup = questionnaire.addItem();
			basicGroup.setLinkId("basic");
			basicGroup.setType(Questionnaire.QuestionnaireItemType.GROUP);
			basicGroup
				.addItem()
				.setLinkId("schoolType")
				.setType(Questionnaire.QuestionnaireItemType.CHOICE)
				.setAnswerValueSet(ID_VS_SCHOOLTYPE)
				.setRequired(true);
		}

		/*
		 * Create response
		 */
		QuestionnaireResponse qa = new QuestionnaireResponse();
		qa.getText().setDiv(new XhtmlNode().setValue("<div>AA</div>")).setStatus(Narrative.NarrativeStatus.GENERATED);
		qa.setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
		qa.setQuestionnaire(ID_ICC_QUESTIONNAIRE_SETUP);
		qa.getSubject().setReference("Patient/123");

		QuestionnaireResponse.QuestionnaireResponseItemComponent basicGroup = qa
			.addItem();
		basicGroup.setLinkId("basic");
		basicGroup
			.addItem()
			.setLinkId("schoolType")
			.addAnswer()
			.setValue(new Coding(SYSTEMURI_ICC_SCHOOLTYPE, CODE_ICC_SCHOOLTYPE_PT, ""));

		when(myValSupport.fetchResource(eq(Questionnaire.class), eq(qa.getQuestionnaire()))).thenReturn(questionnaire);
		when(myValSupport.fetchValueSet(eq(ID_VS_SCHOOLTYPE))).thenReturn(iccSchoolTypeVs);
		when(myValSupport.validateCodeInValueSet(any(), any(), any(), any(), any(), any(ValueSet.class))).thenReturn(new IValidationSupport.CodeValidationResult().setCode(CODE_ICC_SCHOOLTYPE_PT));
		when(myValSupport.fetchCodeSystem(eq(SYSTEMURI_ICC_SCHOOLTYPE))).thenReturn(codeSystem);
		ValidationResult errors = myVal.validateWithResult(qa);

		ourLog.info(errors.toString());
		assertThat(errors.toString()).contains("No issues");
	}


	@AfterAll
	public static void afterClassClearContext() {
		myDefaultValidationSupport.flush();
		myDefaultValidationSupport = null;
		TestUtil.randomizeLocaleAndTimezone();
	}


}
