package com.lu.lib_processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;


// JavaPoet使用测试，生成一个MainActivity
// 参考：https://juejin.im/post/5bc96b63e51d450e4369ba19
/*
*
* public class MainActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
* */
public class JavaPoetTest {

	public static void main(String[] args) {
		ClassName activity = ClassName.get("android.app", "Activity");

		TypeSpec.Builder mainActivityBuilder =
				TypeSpec.classBuilder("MainActivity").addModifiers(Modifier.PUBLIC).superclass(activity);

		ClassName override = ClassName.get("java.lang", "Override");

		ClassName bundle = ClassName.get("android.os", "Bundle");

		ClassName nullable = ClassName.get("android.support.annotation", "Nullable");

		ParameterSpec savedInstanceState = ParameterSpec.builder(bundle, "savedInstanceState")
				.addAnnotation(nullable)
				.build();

		MethodSpec onCreate = MethodSpec.methodBuilder("onCreate")
				.addAnnotation(override)
				.addModifiers(Modifier.PROTECTED)
				.addParameter(savedInstanceState)
				.addStatement("super.onCreate(savedInstanceState)")
				.addStatement("setContentView(R.layout.activity_main)")
				.build();

		TypeSpec mainActivity = mainActivityBuilder.addMethod(onCreate).build();

		JavaFile file = JavaFile.builder("com.test", mainActivity).build();

		try {
			file.writeTo(System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
