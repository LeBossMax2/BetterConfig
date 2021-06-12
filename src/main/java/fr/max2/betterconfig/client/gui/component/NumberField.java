package fr.max2.betterconfig.client.gui.component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.client.gui.ILayoutManager;
import fr.max2.betterconfig.client.util.INumberType;
import fr.max2.betterconfig.client.util.INumberType.Increment;
import fr.max2.betterconfig.client.util.INumberType.Operator;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

/**
 * A widget for entering a number
 * @param <N> the type of accepted number
 */
public abstract class NumberField<N> extends FocusableGui implements INestedGuiComponent
{
	/** The default width of the '+' and '-' buttons */
	protected static final int BUTTON_SIZE = 20;
	/** The spacing between the buttons and the text field */
	protected static final int SPACING = 2;
	/** The list of children ui components */
	protected final List<IGuiComponent> elements;
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
	
	protected int baseX, baseY, w, h;
	protected ILayoutManager layout;

	public NumberField(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title, INumberType<N> numberType, N value)
	{
		this.numberType = numberType;
		this.baseX = x;
		this.baseY = y;
		this.w = width;
		this.h = height;
		this.inputField = new TextField(fontRenderer, x + BUTTON_SIZE + SPACING + 1, y + 1, width - 2 * (BUTTON_SIZE + SPACING + 1), height - 2, title)
		{
			@Override
			protected void onValidate(String text)
			{
				N value = getValue();
				if (!doCorrection(value))
				{
					String fixedText = fixInput(text);
					if (!text.equals(fixedText))
						setValue(value); // Force the text in the input field to be fixed
					
					NumberField.this.onValidate(value);
				}
			}
		};
		this.minusButton = new Button(
			x, y, BUTTON_SIZE, height,
			Increment.NORMAL.getMinusText(),
			thisButton -> applyOperator(Operator.MINUS));
		this.plusButton = new Button(
			x + width - BUTTON_SIZE, y, BUTTON_SIZE, height,
			Increment.NORMAL.getPlusText(),
			thisButton -> applyOperator(Operator.PLUS));
		this.elements = Arrays.asList(this.minusButton, this.inputField, this.plusButton);
		this.inputField.setValidator(this::isValid);
		this.setValue(value);
	}
	
	// Layout
	
	@Override
	public void setLayoutManager(ILayoutManager manager)
	{
		this.layout = manager;
		INestedGuiComponent.super.setLayoutManager(manager);
	}

	@Override
	public int getWidth()
	{
		return this.w;
	}

	@Override
	public int getHeight()
	{
		return this.h;
	}

	/** Sets the x position of this button relative to the layout position */
	public void setX(int x)
	{
		this.baseX = x;
		this.inputField.setX(x + BUTTON_SIZE + SPACING + 1);
		this.minusButton.setX(x);
		this.plusButton.setX(x + this.w - BUTTON_SIZE);
	}

	/** Sets the y position of this button relative to the layout position */
	public void setY(int y)
	{
		this.baseY = y;
		this.inputField.setY(y + 1);
		this.minusButton.setY(y);
		this.plusButton.setY(y);
	}
	
	@Override
	public List<? extends IGuiComponent> getEventListeners()
	{
		return this.elements;
	}
	
	@Override
	public boolean isMouseOver(double mouseX, double mouseY)
	{
		int x = this.baseX + this.layout.getLayoutX();
		int y = this.baseY + this.layout.getLayoutY();
		return mouseX >= x
		    && mouseY >= y
		    && mouseX < x + this.w
		    && mouseY < y + this.h;
	}
	
	// Rendering
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.updateIncrement();
		INestedGuiComponent.super.render(matrixStack, mouseX, mouseY, partialTicks);
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
	public void setValue(N value)
	{
		this.inputField.setText(this.numberType.intoString(value));
	}
	
	/** Gets the number from the field */
	public N getValue()
	{
		return tryConvert(this.inputField.getText()).get();
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
