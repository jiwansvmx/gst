import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File

enum class NodeKind {
    Document,
    OperationDefinition,
    FragmentDefinition,
    VariableDefinition,
    Name,
    SelectionSet,
    Field,
    Argument,
    Variable,
    Directives,
    NonNullType,
    NamedType,
    FragmentSpread,
    IntValue,
    FloatValue,
    StringValue,
    EnumValue,
}

enum class Operation {
    Query,
    Mutation,
}

@JsonClass(generateAdapter = true)
open class Type(kind: NodeKind) : Node(kind)

@JsonClass(generateAdapter = true)
class NamedType(
    val name: Name
) : Type(NodeKind.NamedType)

@JsonClass(generateAdapter = true)
class NonNullType(
    val type: NamedType
) : Type(NodeKind.NonNullType)

@JsonClass(generateAdapter = true)
class Name(
    val value: String,
) : Node(NodeKind.Name)

@JsonClass(generateAdapter = true)
open class Node(
    val kind: NodeKind
)

@JsonClass(generateAdapter = true)
class Document(
    val definitions: List<TopLevelDefinition>,
) : Node(NodeKind.Document)

@JsonClass(generateAdapter = true)
open class Definition(
    kind: NodeKind,
    open val name: Name?,
    val directives: List<Directives>,
) : Node(kind)

@JsonClass(generateAdapter = true)
open class TopLevelDefinition(
    kind: NodeKind,
    name: Name?,
    directives: List<Directives>,
    val selectionSet: SelectionSet,
) : Definition(kind, name, directives)

@JsonClass(generateAdapter = true)
class OperationDefinition(
    name: Name?,
    directives: List<Directives>,
    selectionSet: SelectionSet,
    val operation: Operation,
    val variableDefinition: List<Definition>,
) : TopLevelDefinition(NodeKind.OperationDefinition, name, directives, selectionSet)

@JsonClass(generateAdapter = true)
class VariableDefinition(
    directives: List<Directives>,
    val variable: Variable,
    val type: Type,
    val defaultValue: String?,
) : Definition(NodeKind.VariableDefinition, null, directives)

@JsonClass(generateAdapter = true)
class FragmentDefinition(
    override var name: Name,
    selectionSet: SelectionSet,
    directives: List<Directives>,
    val typeCondition: NamedType,
) : TopLevelDefinition(NodeKind.FragmentDefinition, name, directives, selectionSet)

@JsonClass(generateAdapter = true)
open class Selection(
    kind: NodeKind,
    val name: Name,
    val directives: List<Directives>
) : Node(kind)

@JsonClass(generateAdapter = true)
class SelectionSet(
    val selections: List<Selection>
) : Node(NodeKind.SelectionSet)

@JsonClass(generateAdapter = true)
class Field(
    name: Name,
    directives: List<Directives>,
    val alias: String?,
    val arguments: List<Argument>,
    val selectionSet: SelectionSet?,
) : Selection(NodeKind.Field, name, directives)

@JsonClass(generateAdapter = true)
class FragmentSpread(
    name: Name,
    directives: List<Directives>
) : Selection(NodeKind.FragmentSpread, name, directives)

@JsonClass(generateAdapter = true)
open class ArgumentValue(
    kind: NodeKind,
    open val name: Name? = null,
) : Node(kind)

@JsonClass(generateAdapter = true)
class Variable(
    override var name: Name,
) : ArgumentValue(NodeKind.Variable, name)

@JsonClass(generateAdapter = true)
open class Literal(
    kind: NodeKind,
    val value: String,
) : ArgumentValue(kind)

@JsonClass(generateAdapter = true)
class IntValue(
    value: String,
) : Literal(NodeKind.IntValue, value)

@JsonClass(generateAdapter = true)
class FloatValue(
    value: String,
) : Literal(NodeKind.FloatValue, value)

@JsonClass(generateAdapter = true)
class StringValue(
    value: String,
) : Literal(NodeKind.StringValue, value)

@JsonClass(generateAdapter = true)
class EnumValue(
    value: String,
) : Literal(NodeKind.EnumValue, value)

@JsonClass(generateAdapter = true)
class Argument(
    val name: Name,
    val value: ArgumentValue
) : Node(NodeKind.Argument)


@JsonClass(generateAdapter = true)
class Directives() : Node(NodeKind.Directives)

fun main(args: Array<String>) {
    val moshi = Moshi.Builder().build();
    val astJsonString = File(ClassLoader.getSystemResource("ast.json").file).readText()
    val documentAdapter = DocumentJsonAdapter(moshi)
    try {
        val document = documentAdapter.fromJson(astJsonString)!!
        println(document.definitions)
    } catch (exception: Exception) {
        println(exception.message)
    }


}