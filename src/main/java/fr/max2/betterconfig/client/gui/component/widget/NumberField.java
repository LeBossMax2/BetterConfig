package fr.max2.betterconfig.client.gui.component.widget;

import java.util.Arrays;
import java.util.Optional;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.betterconfig.client.gui.component.CompositeComponent;
import fr.max2.betterconfig.client.util.INumberType;
import fr.max2.betterconfig.client.util.INumberType.Increment;
import fr.max2.betterconfig.client.util.INumberType.Operator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A widget for entering a number
 * @param <N> the type of accepted number
 */
public class NumberField<N> extends CompositeComponent
{
	/** The default width of the '+' and '-' buttons */
	public static final int BUTTON_SIZE = 20;
	
	/** The text field to directly enter the number */
	protected final TextField inputField;
	/** The minus button to decrement the number */
	protected final Button minusButton;
	/** The plus button to increment the number */
	protected final Button plusButton;
	/** The type of accepted number */
	protected final INumberType<N> numberType;
	/** The current increment for each button click */
	protected Increment currentIncrement = Increment.NORMAL;

	public NumberField(Font fontRenderer, Component title, INumberType<N> numberType, N value)
	{
		super("number_field");
		this.numberType = numberType;
		this.inputField = new TextField(fontRenderer, title)
		{
			@Override
			protected void onValidate(String text)
			{
				N value = NumberField.this.getValue();
				if (!doCorrection(value))
				{
					String fixedText = fixInput(text);
					if (!text.equals(fixedText))
						NumberField.this.setValue(value); // Force the text in the input field to be fixed
					
					NumberField.this.onValidate(value);
				}
			}
		};
		this.inputField.addClass("number_field:text");
		this.minusButton = new Button(Increment.NORMAL.getMinusText())
				.addOnPressed(() -> applyOperator(Operator.MINUS));
		this.minusButton.addClass("number_field:minus_button");
		this.plusButton = new Button(Increment.NORMAL.getPlusText())
				.addOnPressed(() -> applyOperator(Operator.PLUS));
		this.plusButton.addClass("number_field:plus_button");
		this.children.addAll(Arrays.asList(this.minusButton, this.inputField, this.plusButton));
		this.inputField.widget.setFilter(this::isValid);
		this.setValue(value);
	}
	
	// Rendering
	
	@Override
	protected void onRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.updateIncrement();
		super.onRender(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	// Value manipulation methods
	
	/** Updates the button text with the correct increment */
	protected void updateIncrement()
	{
		Increment inc;
		if (Screen.hasControlDown())
		{
			inc = Increment.HIGH;
		}
		else if (Screen.hasAltDown())
		{
			inc = Increment.LOW;
		}
		else
		{
			inc = Increment.NORMAL;
		}
		
		if (inc != this.currentIncrement)
		{
			this.currentIncrement = inc;
			this.minusButton.setMessage(inc.getMinusText());
			this.plusButton.setMessage(inc.getPlusText());
		}
	}
	
	/** Sets the number in the field */
	protected void setValue(N value)
	{
		this.inputField.setValue(this.numberType.intoString(value));
	}
	
	/** Gets the number from the field */
	public N getValue()
	{
		return tryConvert(this.inputField.getValue()).get();
	}
	
	/** Fixes the input to a valid one if it is almost valid */
	private static String fixInput(String input)
	{
		String res = input;
		if (Strings.isNullOrEmpty(input) || input.equals("-"))
			res = "0";
		
		else if (input.equals(".") || input.equals("-."))
			res = "0.0";
		
		return res;
	}
	
	/** Tries to convert the given input string to a number */
	private Optional<N> tryConvert(String input)
	{
		return this.numberType.tryParse(fixInput(input));
	}
	
	/** Checks if the given input is valid */
	private boolean isValid(String input)
	{
		return tryConvert(input).isPresent();
	}
	
	/** Applies the given operation to the current value */
	protected void applyOperator(Operator operation)
	{
		N newValue = this.numberType.applyOperation(this.getValue(), operation, this.currentIncrement);
		if (!doCorrection(newValue))
		{
			this.setValue(newValue);
			this.onValidate(newValue);
		}
	}
	
	/**
	 * Tries to correct the given value to an accepted one and sets the value to this new value
	 * @return true if a correction had to be done, false otherwise
	 */
	private boolean doCorrection(N value)
	{
		N corrected = correct(value);
		if (!value.equals(corrected))
		{
			this.setValue(corrected);
			this.onValidate(corrected);
			return true;
		}
		return false;
	}
	
	/**
	 * Corrects the given value to a valid one
	 * @param value the number to correct
	 * @return the corrected value
	 */
	protected N correct(N value)
	{
		return value;
	}
	
	/** A function called when a new value is entered by the user */
	protected void onValidate(N value)
	{ }
}
