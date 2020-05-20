package com.lu.lib_processor;

import com.lu.lib_annotations.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class BindProcessor extends AbstractProcessor {
	private Filer    mFiler;
	private Elements mElementUtils;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);
		mFiler = processingEnvironment.getFiler();
		mElementUtils = processingEnv.getElementUtils();
	}

	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
		//------------获取注解-----------
		Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
		//LinkedHashMap输出和输入的顺序相同，先输入就先输出
		Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();
		for (Element element : elements) {
			//这里会把所有跟注解有关的field全部拿到，包括各个类中的field,也就是说在编译时，
			// 项目中所有涉及到这个注解的地方的所有field都在这个Set中返回了，我们需要手动进行分类
			System.out.println("[lu]###" + element.getSimpleName());// [lu]###mTv
			//得到的enclosingElement是这个field所在类的类名
			Element enclosingElement = element.getEnclosingElement();
			// ------------enclosingElement-----------MainActivity
			System.out.println("------------enclosingElement-----------" + enclosingElement.getSimpleName());
			//以类名为key值，存储一个类中所有的field到集合elementsMap中
			List<Element> bindViewElements = elementsMap.get(enclosingElement);
			if (bindViewElements == null) {
				bindViewElements = new ArrayList<>();
				elementsMap.put(enclosingElement, bindViewElements);
			}
			bindViewElements.add(element);
		}

		for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
			Element       enclosingElement = entry.getKey();// MainActivity
			List<Element> bindViewElements = entry.getValue();// field: TextView mTv;

			ClassName unbinderClassName = ClassName.get("com.lu.lib_annotation", "Unbinder");
			// ------------Unbinder-----------Unbinder
			System.out.println("------------Unbinder-----------" + unbinderClassName.simpleName());
			//得到类名的字符串
			String    activityName      = enclosingElement.getSimpleName().toString();
			ClassName activityClassName = ClassName.bestGuess(activityName);
			//拼装这一行代码：public final class xxx_ViewBinding implements Unbinder
			TypeSpec.Builder classBuilder = TypeSpec.classBuilder(activityName + "_ViewBinding")
					//类名前添加public final
					.addModifiers(Modifier.FINAL, Modifier.PUBLIC)
					//添加类的实现接口
					.addSuperinterface(unbinderClassName)
					//添加一个成员变量，这个名字target是仿照butterknife
					.addField(activityClassName, "target", Modifier.PRIVATE);

			//实现Unbinder的方法
			//CallSuper这个注解不像Override可以直接拿到，需要用这种方式
			ClassName callSuperClass = ClassName.get("android.support.annotation", "CallSuper");
			MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
					//和你创建的Unbinder中的方法名保持一致
					.addAnnotation(Override.class)
					.addAnnotation(callSuperClass)
					.addModifiers(Modifier.FINAL, Modifier.PUBLIC);

			//添加构造函数
			MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder().addParameter(activityClassName, "target");
			constructMethodBuilder.addStatement("this.target = target");

			for (Element bindViewElement : bindViewElements) {
				String fieldName = bindViewElement.getSimpleName().toString();

				//在构造方法中添加初始化代码
				ClassName utilsClassName = ClassName.get("com.lu.lib_annotation", "Utils");
				ClassName tv = ClassName.get("android.widget", "TextView");
				BindView  annotation     = bindViewElement.getAnnotation(BindView.class);
				if (annotation != null) {
					int resId = annotation.value();
					constructMethodBuilder.addStatement("target.$L = ($T) target.findViewById($L)"
							, fieldName, tv, resId);

					//在unbind方法中添加代码 target.textView1 = null;
					//不能用addCode,因为它不会在每一行代码后加分号和换行
					unbindMethodBuilder.addStatement("target.$L = null", fieldName);
				}

			}
			classBuilder.addMethod(constructMethodBuilder.build());
			classBuilder.addMethod(unbindMethodBuilder.build());

			try {
				//得到包名
				String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();

				JavaFile.builder(packageName, classBuilder.build())
						//添加类的注释
						.addFileComment("butterknife 自动生成").build().writeTo(mFiler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;

	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();

	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> types = new LinkedHashSet<>();
		for (Class<? extends Annotation> annotation : getSupportAnnotations()) {
			types.add(annotation.getCanonicalName());
		}
		return types;
	}

	private Set<Class<? extends Annotation>> getSupportAnnotations() {
		Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
		annotations.add(BindView.class);
		return annotations;
	}
}
