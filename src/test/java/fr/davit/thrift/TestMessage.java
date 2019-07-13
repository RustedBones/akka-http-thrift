/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package fr.davit.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2019-01-31")
public class TestMessage implements org.apache.thrift.TBase<TestMessage, TestMessage._Fields>, java.io.Serializable, Cloneable, Comparable<TestMessage> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TestMessage");

  private static final org.apache.thrift.protocol.TField STRING_FIELD_FIELD_DESC = new org.apache.thrift.protocol.TField("stringField", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField NUMBER_FIELD_FIELD_DESC = new org.apache.thrift.protocol.TField("numberField", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TestMessageStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TestMessageTupleSchemeFactory();

  public java.lang.String stringField; // required
  public int numberField; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STRING_FIELD((short)1, "stringField"),
    NUMBER_FIELD((short)2, "numberField");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // STRING_FIELD
          return STRING_FIELD;
        case 2: // NUMBER_FIELD
          return NUMBER_FIELD;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __NUMBERFIELD_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STRING_FIELD, new org.apache.thrift.meta_data.FieldMetaData("stringField", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.NUMBER_FIELD, new org.apache.thrift.meta_data.FieldMetaData("numberField", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TestMessage.class, metaDataMap);
  }

  public TestMessage() {
  }

  public TestMessage(
    java.lang.String stringField,
    int numberField)
  {
    this();
    this.stringField = stringField;
    this.numberField = numberField;
    setNumberFieldIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TestMessage(TestMessage other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetStringField()) {
      this.stringField = other.stringField;
    }
    this.numberField = other.numberField;
  }

  public TestMessage deepCopy() {
    return new TestMessage(this);
  }

  @Override
  public void clear() {
    this.stringField = null;
    setNumberFieldIsSet(false);
    this.numberField = 0;
  }

  public java.lang.String getStringField() {
    return this.stringField;
  }

  public TestMessage setStringField(java.lang.String stringField) {
    this.stringField = stringField;
    return this;
  }

  public void unsetStringField() {
    this.stringField = null;
  }

  /** Returns true if field stringField is set (has been assigned a value) and false otherwise */
  public boolean isSetStringField() {
    return this.stringField != null;
  }

  public void setStringFieldIsSet(boolean value) {
    if (!value) {
      this.stringField = null;
    }
  }

  public int getNumberField() {
    return this.numberField;
  }

  public TestMessage setNumberField(int numberField) {
    this.numberField = numberField;
    setNumberFieldIsSet(true);
    return this;
  }

  public void unsetNumberField() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __NUMBERFIELD_ISSET_ID);
  }

  /** Returns true if field numberField is set (has been assigned a value) and false otherwise */
  public boolean isSetNumberField() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __NUMBERFIELD_ISSET_ID);
  }

  public void setNumberFieldIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __NUMBERFIELD_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case STRING_FIELD:
      if (value == null) {
        unsetStringField();
      } else {
        setStringField((java.lang.String)value);
      }
      break;

    case NUMBER_FIELD:
      if (value == null) {
        unsetNumberField();
      } else {
        setNumberField((java.lang.Integer)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case STRING_FIELD:
      return getStringField();

    case NUMBER_FIELD:
      return getNumberField();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case STRING_FIELD:
      return isSetStringField();
    case NUMBER_FIELD:
      return isSetNumberField();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof TestMessage)
      return this.equals((TestMessage)that);
    return false;
  }

  public boolean equals(TestMessage that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_stringField = true && this.isSetStringField();
    boolean that_present_stringField = true && that.isSetStringField();
    if (this_present_stringField || that_present_stringField) {
      if (!(this_present_stringField && that_present_stringField))
        return false;
      if (!this.stringField.equals(that.stringField))
        return false;
    }

    boolean this_present_numberField = true;
    boolean that_present_numberField = true;
    if (this_present_numberField || that_present_numberField) {
      if (!(this_present_numberField && that_present_numberField))
        return false;
      if (this.numberField != that.numberField)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetStringField()) ? 131071 : 524287);
    if (isSetStringField())
      hashCode = hashCode * 8191 + stringField.hashCode();

    hashCode = hashCode * 8191 + numberField;

    return hashCode;
  }

  @Override
  public int compareTo(TestMessage other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetStringField()).compareTo(other.isSetStringField());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStringField()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.stringField, other.stringField);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetNumberField()).compareTo(other.isSetNumberField());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNumberField()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.numberField, other.numberField);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("TestMessage(");
    boolean first = true;

    sb.append("stringField:");
    if (this.stringField == null) {
      sb.append("null");
    } else {
      sb.append(this.stringField);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("numberField:");
    sb.append(this.numberField);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TestMessageStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TestMessageStandardScheme getScheme() {
      return new TestMessageStandardScheme();
    }
  }

  private static class TestMessageStandardScheme extends org.apache.thrift.scheme.StandardScheme<TestMessage> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TestMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STRING_FIELD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.stringField = iprot.readString();
              struct.setStringFieldIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NUMBER_FIELD
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.numberField = iprot.readI32();
              struct.setNumberFieldIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TestMessage struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.stringField != null) {
        oprot.writeFieldBegin(STRING_FIELD_FIELD_DESC);
        oprot.writeString(struct.stringField);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(NUMBER_FIELD_FIELD_DESC);
      oprot.writeI32(struct.numberField);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TestMessageTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TestMessageTupleScheme getScheme() {
      return new TestMessageTupleScheme();
    }
  }

  private static class TestMessageTupleScheme extends org.apache.thrift.scheme.TupleScheme<TestMessage> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TestMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetStringField()) {
        optionals.set(0);
      }
      if (struct.isSetNumberField()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetStringField()) {
        oprot.writeString(struct.stringField);
      }
      if (struct.isSetNumberField()) {
        oprot.writeI32(struct.numberField);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TestMessage struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.stringField = iprot.readString();
        struct.setStringFieldIsSet(true);
      }
      if (incoming.get(1)) {
        struct.numberField = iprot.readI32();
        struct.setNumberFieldIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
