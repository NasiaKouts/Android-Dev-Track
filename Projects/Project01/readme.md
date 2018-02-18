# Project 1

In this project we had to create the JSON parser, and then using the sandwich object created the info taken by JSON, we had to populate the details activity. Also, we had to make any modification to the detail activity's layout, in order to show the sandwich's info in a well organized way.

## Notes about the implementation

- I decided to use a divider between each detail section in order to make them stand out clearly.
- Also, I used bold text style to all detail label sections.
- Regarding the "also known as" section, I decided that in case there was no alternative name provided, not to show the section at all. Thus, why I set the corresponding views' visibility to GONE.
- In addition, in any other detail section, in case there was no info provided though json, I setted the corresponding view's text to _not available_, using italic style to make it stand out, in order to inform the user about the lack of info.
- I also decided to put the alternative names next to the corresponding label, using italic style, whereas in rest detail sections the context appears bellow the corresponding label. I simply thought that would may look better, cause the "also known as" category, seems to be a totally different type of detail.
- Lastly, I added the Up navigation from detail activity to its parent activity, the main activity.