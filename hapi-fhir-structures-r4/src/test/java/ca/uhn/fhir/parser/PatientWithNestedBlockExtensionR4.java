package ca.uhn.fhir.parser;

import ca.uhn.fhir.model.api.annotation.Block;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.Extension;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.util.ElementUtil;
import org.hl7.fhir.r4.model.BackboneElement;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.StringType;

/**
 * FUT1-25450 accept Extension.id on block-typed declared extensions.
 *
 * <p>A custom resource shaped like the profiled resources in use downstream: a complex declared
 * extension modelled as a {@link Block}, itself holding a nested block, plus a declared extension
 * with a plain value for contrast.
 */
@ResourceDef(name = "Patient")
public class PatientWithNestedBlockExtensionR4 extends Patient {

	private static final long serialVersionUID = 1L;

	@Child(name = "item")
	@Extension(url = "http://acme.org/item", definedLocally = false, isModifier = false)
	private ItemExtension myItem;

	/**
	 * A declared extension with a single value[x] - out of scope for Extension.id support.
	 */
	@Child(name = "label")
	@Extension(url = "http://acme.org/label", definedLocally = false, isModifier = false)
	private StringType myLabel;

	public ItemExtension getItem() {
		return myItem;
	}

	public void setItem(ItemExtension theItem) {
		myItem = theItem;
	}

	public StringType getLabel() {
		return myLabel;
	}

	public void setLabel(StringType theLabel) {
		myLabel = theLabel;
	}

	@Override
	public boolean isEmpty() {
		return super.isEmpty() && ElementUtil.isEmpty(myItem, myLabel);
	}

	@Block
	public static class ItemExtension extends BackboneElement {

		private static final long serialVersionUID = 1L;

		@Child(name = "description")
		@Extension(url = "description", definedLocally = false, isModifier = false)
		private StringType myDescription;

		@Child(name = "linkage")
		@Extension(url = "linkage", definedLocally = false, isModifier = false)
		private LinkageExtension myLinkage;

		public StringType getDescription() {
			return myDescription;
		}

		public void setDescription(StringType theDescription) {
			myDescription = theDescription;
		}

		public LinkageExtension getLinkage() {
			return myLinkage;
		}

		public void setLinkage(LinkageExtension theLinkage) {
			myLinkage = theLinkage;
		}

		@Override
		public ItemExtension copy() {
			ItemExtension copy = new ItemExtension();
			copy.setId(getId());
			copy.myDescription = myDescription;
			copy.myLinkage = myLinkage;
			return copy;
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty() && ElementUtil.isEmpty(myDescription, myLinkage);
		}
	}

	@Block
	public static class LinkageExtension extends BackboneElement {

		private static final long serialVersionUID = 1L;

		@Child(name = "reference")
		@Extension(url = "reference", definedLocally = false, isModifier = false)
		private StringType myReference;

		public StringType getReference() {
			return myReference;
		}

		public void setReference(StringType theReference) {
			myReference = theReference;
		}

		@Override
		public LinkageExtension copy() {
			LinkageExtension copy = new LinkageExtension();
			copy.setId(getId());
			copy.myReference = myReference;
			return copy;
		}

		@Override
		public boolean isEmpty() {
			return super.isEmpty() && ElementUtil.isEmpty(myReference);
		}
	}
}
