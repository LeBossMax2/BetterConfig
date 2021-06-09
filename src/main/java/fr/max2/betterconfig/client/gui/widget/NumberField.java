package fr.max2.betterconfig.client.gui.widget;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;

import fr.max2.betterconfig.util.INumberType;
import fr.max2.betterconfig.util.INumberType.Increment;
import fr.max2.betterconfig.util.INumberType.Operator;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public abstract class NumberField<N> extends FocusableGui implements INestedUIElement
{
	protected static final int BUTTON_SIZE = 20;
	//TODO protected static final int SPACING = 2;
	protected final List<IUIElement> elements;
	protected final TextField inputField;
	protected final Button minusButton;
	protected final Button plusButton;
	protected final INumberType<N> numberType;
	protected Increment currentIncrement = Increment.NORMAL;

	public NumberField(FontRenderer fontRenderer, int x, int y, int width, int height, ITextComponent title, INumberType<N> numberType, N value)
	{
		this.numberType = numberType;
		this.inputField = new TextField(fontRenderer, x + BUTTON_SIZE + 3, y + 1, width - 2 * BUTTON_SIZE - 6, height - 2, title)
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
		this.minusButton = new Button(x, y, BUTTON_SIZE, height,
			Increment.NORMAL.getMinusText(),
			thizz -> applyOperator(Operator.MINUS), Button.NO_TOOLTIP);
		this.plusButton = new Button(x + width - BUTTON_SIZE, y, BUTTON_SIZE, height,
			Increment.NORMAL.getPlusText(),
			thizz -> applyOperator(Operator.PLUS), Button.NO_TOOLTIP);
		this.elements = Arrays.asList(this.minusButton, this.inputField, this.plusButton);
		this.inputField.setValidator(this::isValid);
		this.setValue(value);
	}
	
	@Override
	public List<? extends IUIElement> getEventListeners()
	{
		return this.elements;
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
	{
		this.updateIncrement();
		INestedUIElement.super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
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
	
	public void setValue(N value)
	{
		this.inputField.setText(this.numberType.intoString(value));
	}
	
	public N getValue()
	{
		return tryConvert(this.inputField.getText()).get();
	}
	
	private static String fixInput(String input)
	{
		String res = input;
		if (Strings.isNullOrEmpty(input) || input.equals("-"))
			res = "0";
		
		else if (input.equals(".") || input.equals("-."))
			res = "0.0";
		
		return res;
	}
	
	private Optional<N> tryConvert(String input)
	{
		return this.numberType.tryParse(fixInput(input));
	}
	
	private boolean isValid(String input)
	{
		return tryConvert(input).isPresent();
	}
	
	protected void applyOperator(Operator operation)
	{
		N newValue = this.numberType.applyOperation(this.getValue(), operation, this.currentIncrement);
		if (!doCorrection(newValue))
		{
			this.setValue(newValue);
			this.onValidate(newValue);
		}
	}
	
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
	
	protected N correct(N value)
	{
		return value;
	}
	
	protected void onValidate(N value)
	{ }
}
