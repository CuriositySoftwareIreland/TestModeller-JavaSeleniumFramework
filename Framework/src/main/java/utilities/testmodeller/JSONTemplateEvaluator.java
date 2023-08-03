package utilities.testmodeller;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;

import java.io.IOException;

public class JSONTemplateEvaluator {
    public static String EvaluateMessageTemplate(String msg) {
        Handlebars handlebars = new Handlebars();
        // Register the range helper
        handlebars.registerHelper("range", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                int start = (int) context;
                int end = (int) options.param(0);
                StringBuilder result = new StringBuilder();

                for (int i = start; i <= end; i++) {
                    result.append(options.fn(i));
                }

                return result.toString();
            }
        });

        handlebars.registerHelper("equals", new Helper<String>() {
            @Override
            public Object apply(String s1, Options options) {
                String s2 = options.param(0);
                return s1.equals(s2);
            }
        });

        handlebars.registerHelper("ifEq", new Helper<Object>() {
            @Override
            public CharSequence apply(Object context, Options options) throws IOException {
                if (context.equals(options.param(0))) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("ifGreaterThan", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                if (context > (Integer) options.param(0)) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("ifLessThan", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                if (context < (Integer) options.param(0)) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("ifNotEqualTo", new Helper<Object>() {
            @Override
            public CharSequence apply(Object context, Options options) throws IOException {
                if (!context.equals(options.param(0))) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("ifGreaterThanOrEqualTo", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                if (context >= (Integer) options.param(0)) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("ifLessThanOrEqualTo", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                if (context <= (Integer) options.param(0)) {
                    return options.fn(context);
                } else {
                    return options.inverse(context);
                }
            }
        });

        handlebars.registerHelper("range", new Helper<Integer>() {
            @Override
            public CharSequence apply(Integer context, Options options) throws IOException {
                int start = context;
                int end = (Integer) options.param(0);
                StringBuilder sb = new StringBuilder();

                for (int i = start; i <= end; i++) {
                    sb.append(options.fn(i));
                }

                return sb.toString();
            }
        });

        try {
            Template template = handlebars.compileInline(msg);

            String resolvedMsg = template.apply(null);

            return resolvedMsg;
        } catch (IOException e) {
            return msg;
        }
    }
}
