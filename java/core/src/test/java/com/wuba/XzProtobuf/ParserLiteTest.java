// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// https://developers.google.com/protocol-buffers/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.github.XzProtobuf.protobuf_3_11_1;

import com.github.XzProtobuf.protobuf_3_11_1.UnittestLite.TestAllTypesLite;
import com.github.XzProtobuf.protobuf_3_11_1.UnittestLite.TestPackedExtensionsLite;
import com.github.XzProtobuf.protobuf_3_11_1.UnittestLite.TestParsingMergeLite;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;

public class ParserLiteTest extends TestCase {
  private void assertRoundTripEquals(MessageLite message, ExtensionRegistryLite registry)
      throws Exception {
    final byte[] data = message.toByteArray();
    final int offset = 20;
    final int length = data.length;
    final int padding = 30;
    Parser<? extends MessageLite> parser = message.getParserForType();
    assertEquals(message, parser.parseFrom(data, registry));
    assertEquals(
        message,
        parser.parseFrom(generatePaddingArray(data, offset, padding), offset, length, registry));
    assertEquals(message, parser.parseFrom(message.toByteString(), registry));
    assertEquals(message, parser.parseFrom(new ByteArrayInputStream(data), registry));
    assertEquals(message, parser.parseFrom(CodedInputStream.newInstance(data), registry));
    assertEquals(
        message, parser.parseFrom(message.toByteString().asReadOnlyByteBuffer(), registry));
  }

  @SuppressWarnings("unchecked")
  private void assertRoundTripEquals(MessageLite message) throws Exception {
    final byte[] data = message.toByteArray();
    final int offset = 20;
    final int length = data.length;
    final int padding = 30;

    Parser<MessageLite> parser = (Parser<MessageLite>) message.getParserForType();
    assertEquals(message, parser.parseFrom(data));
    assertEquals(
        message, parser.parseFrom(generatePaddingArray(data, offset, padding), offset, length));
    assertEquals(message, parser.parseFrom(message.toByteString()));
    assertEquals(message, parser.parseFrom(new ByteArrayInputStream(data)));
    assertEquals(message, parser.parseFrom(CodedInputStream.newInstance(data)));
    assertEquals(message, parser.parseFrom(message.toByteString().asReadOnlyByteBuffer()));
  }

  private byte[] generatePaddingArray(byte[] data, int offset, int padding) {
    byte[] result = new byte[offset + data.length + padding];
    System.arraycopy(data, 0, result, offset, data.length);
    return result;
  }

  public void testParseExtensionsLite() throws Exception {
    assertRoundTripEquals(
        TestUtilLite.getAllLiteExtensionsSet(), TestUtilLite.getExtensionRegistryLite());
  }

  public void testParsePacked() throws Exception {
    assertRoundTripEquals(TestUtil.getPackedSet());
    assertRoundTripEquals(TestUtil.getPackedExtensionsSet(), TestUtil.getExtensionRegistry());
  }

  public void testParsePackedLite() throws Exception {
    assertRoundTripEquals(
        TestUtilLite.getLitePackedExtensionsSet(), TestUtilLite.getExtensionRegistryLite());
  }

  public void testParseDelimitedToLite() throws Exception {
    // Write MessageLite with packed extension fields.
    TestPackedExtensionsLite packedMessage = TestUtilLite.getLitePackedExtensionsSet();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    packedMessage.writeDelimitedTo(output);
    packedMessage.writeDelimitedTo(output);

    InputStream input = new ByteArrayInputStream(output.toByteArray());
    assertEquals(
        packedMessage,
        packedMessage
            .getParserForType()
            .parseDelimitedFrom(input, TestUtilLite.getExtensionRegistryLite()));
    assertEquals(
        packedMessage,
        packedMessage
            .getParserForType()
            .parseDelimitedFrom(input, TestUtilLite.getExtensionRegistryLite()));
  }

  /** Helper method for {@link #testParsingMergeLite()}. */
  private void assertMessageMerged(TestAllTypesLite allTypes) throws Exception {
    assertEquals(3, allTypes.getOptionalInt32());
    assertEquals(2, allTypes.getOptionalInt64());
    assertEquals("hello", allTypes.getOptionalString());
  }

  public void testParsingMergeLite() throws Exception {
    // Build messages.
    TestAllTypesLite.Builder builder = TestAllTypesLite.newBuilder();
    TestAllTypesLite msg1 = builder.setOptionalInt32(1).build();
    builder.clear();
    TestAllTypesLite msg2 = builder.setOptionalInt64(2).build();
    builder.clear();
    TestAllTypesLite msg3 = builder.setOptionalInt32(3).setOptionalString("hello").build();

    // Build groups.
    TestParsingMergeLite.RepeatedFieldsGenerator.Group1 optionalG1 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group1.newBuilder().setField1(msg1).build();
    TestParsingMergeLite.RepeatedFieldsGenerator.Group1 optionalG2 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group1.newBuilder().setField1(msg2).build();
    TestParsingMergeLite.RepeatedFieldsGenerator.Group1 optionalG3 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group1.newBuilder().setField1(msg3).build();
    TestParsingMergeLite.RepeatedFieldsGenerator.Group2 repeatedG1 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group2.newBuilder().setField1(msg1).build();
    TestParsingMergeLite.RepeatedFieldsGenerator.Group2 repeatedG2 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group2.newBuilder().setField1(msg2).build();
    TestParsingMergeLite.RepeatedFieldsGenerator.Group2 repeatedG3 =
        TestParsingMergeLite.RepeatedFieldsGenerator.Group2.newBuilder().setField1(msg3).build();

    // Assign and serialize RepeatedFieldsGenerator.
    ByteString data =
        TestParsingMergeLite.RepeatedFieldsGenerator.newBuilder()
            .addField1(msg1)
            .addField1(msg2)
            .addField1(msg3)
            .addField2(msg1)
            .addField2(msg2)
            .addField2(msg3)
            .addField3(msg1)
            .addField3(msg2)
            .addField3(msg3)
            .addGroup1(optionalG1)
            .addGroup1(optionalG2)
            .addGroup1(optionalG3)
            .addGroup2(repeatedG1)
            .addGroup2(repeatedG2)
            .addGroup2(repeatedG3)
            .addExt1(msg1)
            .addExt1(msg2)
            .addExt1(msg3)
            .addExt2(msg1)
            .addExt2(msg2)
            .addExt2(msg3)
            .build()
            .toByteString();

    // Parse TestParsingMergeLite.
    ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
    UnittestLite.registerAllExtensions(registry);
    TestParsingMergeLite parsingMerge = TestParsingMergeLite.parser().parseFrom(data, registry);

    // Required and optional fields should be merged.
    assertMessageMerged(parsingMerge.getRequiredAllTypes());
    assertMessageMerged(parsingMerge.getOptionalAllTypes());
    assertMessageMerged(parsingMerge.getOptionalGroup().getOptionalGroupAllTypes());
    assertMessageMerged(parsingMerge.getExtension(TestParsingMergeLite.optionalExt));

    // Repeated fields should not be merged.
    assertEquals(3, parsingMerge.getRepeatedAllTypesCount());
    assertEquals(3, parsingMerge.getRepeatedGroupCount());
    assertEquals(3, parsingMerge.getExtensionCount(TestParsingMergeLite.repeatedExt));
  }
}
