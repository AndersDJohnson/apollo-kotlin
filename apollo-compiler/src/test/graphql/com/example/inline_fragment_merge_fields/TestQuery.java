// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.inline_fragment_merge_fields;

import com.apollographql.apollo.api.Operation;
import com.apollographql.apollo.api.OperationName;
import com.apollographql.apollo.api.Query;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.ResponseField;
import com.apollographql.apollo.api.ScalarTypeAdapters;
import com.apollographql.apollo.api.internal.OperationRequestBodyComposer;
import com.apollographql.apollo.api.internal.Optional;
import com.apollographql.apollo.api.internal.QueryDocumentMinifier;
import com.apollographql.apollo.api.internal.ResponseFieldMapper;
import com.apollographql.apollo.api.internal.ResponseFieldMarshaller;
import com.apollographql.apollo.api.internal.ResponseReader;
import com.apollographql.apollo.api.internal.ResponseWriter;
import com.apollographql.apollo.api.internal.SimpleOperationResponseParser;
import com.apollographql.apollo.api.internal.Utils;
import com.example.inline_fragment_merge_fields.type.CustomType;
import java.io.IOException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TestQuery implements Query<TestQuery.Data, Optional<TestQuery.Data>, Operation.Variables> {
  public static final String OPERATION_ID = "5ff8c88e5dfec4301ed7c0603bae2088ecdd096a8336ed0c2e5d386b08ebe5c5";

  public static final String QUERY_DOCUMENT = QueryDocumentMinifier.minify(
    "query TestQuery {\n"
        + "  hero {\n"
        + "    __typename\n"
        + "    name\n"
        + "    friendsConnection {\n"
        + "      __typename\n"
        + "      edges {\n"
        + "        __typename\n"
        + "        node {\n"
        + "          __typename\n"
        + "          name\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "    ... on Character {\n"
        + "      name\n"
        + "      profileLink\n"
        + "      friendsConnection {\n"
        + "        __typename\n"
        + "        edges {\n"
        + "          __typename\n"
        + "          node {\n"
        + "            __typename\n"
        + "            name\n"
        + "          }\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "}"
  );

  public static final OperationName OPERATION_NAME = new OperationName() {
    @Override
    public String name() {
      return "TestQuery";
    }
  };

  private final Operation.Variables variables;

  public TestQuery() {
    this.variables = Operation.EMPTY_VARIABLES;
  }

  @Override
  public String operationId() {
    return OPERATION_ID;
  }

  @Override
  public String queryDocument() {
    return QUERY_DOCUMENT;
  }

  @Override
  public Optional<TestQuery.Data> wrapData(TestQuery.Data data) {
    return Optional.fromNullable(data);
  }

  @Override
  public Operation.Variables variables() {
    return variables;
  }

  @Override
  public ResponseFieldMapper<TestQuery.Data> responseFieldMapper() {
    return new Data.Mapper();
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public OperationName name() {
    return OPERATION_NAME;
  }

  @Override
  @NotNull
  public Response<Optional<TestQuery.Data>> parse(@NotNull final BufferedSource source,
      @NotNull final ScalarTypeAdapters scalarTypeAdapters) throws IOException {
    return SimpleOperationResponseParser.parse(source, this, scalarTypeAdapters);
  }

  @Override
  @NotNull
  public Response<Optional<TestQuery.Data>> parse(@NotNull final ByteString byteString,
      @NotNull final ScalarTypeAdapters scalarTypeAdapters) throws IOException {
    return parse(new Buffer().write(byteString), scalarTypeAdapters);
  }

  @Override
  @NotNull
  public Response<Optional<TestQuery.Data>> parse(@NotNull final BufferedSource source) throws
      IOException {
    return parse(source, ScalarTypeAdapters.DEFAULT);
  }

  @Override
  @NotNull
  public Response<Optional<TestQuery.Data>> parse(@NotNull final ByteString byteString) throws
      IOException {
    return parse(byteString, ScalarTypeAdapters.DEFAULT);
  }

  @Override
  @NotNull
  public ByteString composeRequestBody(@NotNull final ScalarTypeAdapters scalarTypeAdapters) {
    return OperationRequestBodyComposer.compose(this, false, true, scalarTypeAdapters);
  }

  @NotNull
  @Override
  public ByteString composeRequestBody() {
    return OperationRequestBodyComposer.compose(this, false, true, ScalarTypeAdapters.DEFAULT);
  }

  @Override
  @NotNull
  public ByteString composeRequestBody(final boolean autoPersistQueries,
      final boolean withQueryDocument, @NotNull final ScalarTypeAdapters scalarTypeAdapters) {
    return OperationRequestBodyComposer.compose(this, autoPersistQueries, withQueryDocument, scalarTypeAdapters);
  }

  public static final class Builder {
    Builder() {
    }

    public TestQuery build() {
      return new TestQuery();
    }
  }

  public static class Data implements Operation.Data {
    static final ResponseField[] $responseFields = {
      ResponseField.forObject("hero", "hero", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final Optional<Hero> hero;

    private transient volatile String $toString;

    private transient volatile int $hashCode;

    private transient volatile boolean $hashCodeMemoized;

    public Data(@Nullable Hero hero) {
      this.hero = Optional.fromNullable(hero);
    }

    public Optional<Hero> hero() {
      return this.hero;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeObject($responseFields[0], hero.isPresent() ? hero.get().marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Data{"
          + "hero=" + hero
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Data) {
        Data that = (Data) o;
        return this.hero.equals(that.hero);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= hero.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Data> {
      final Hero.Mapper heroFieldMapper = new Hero.Mapper();

      @Override
      public Data map(ResponseReader reader) {
        final Hero hero = reader.readObject($responseFields[0], new ResponseReader.ObjectReader<Hero>() {
          @Override
          public Hero read(ResponseReader reader) {
            return heroFieldMapper.map(reader);
          }
        });
        return new Data(hero);
      }
    }
  }

  public static class Hero {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("name", "name", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("friendsConnection", "friendsConnection", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forCustomType("profileLink", "profileLink", null, false, CustomType.URL, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String name;

    final @NotNull FriendsConnection friendsConnection;

    final @NotNull Object profileLink;

    private transient volatile String $toString;

    private transient volatile int $hashCode;

    private transient volatile boolean $hashCodeMemoized;

    public Hero(@NotNull String __typename, @NotNull String name,
        @NotNull FriendsConnection friendsConnection, @NotNull Object profileLink) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.name = Utils.checkNotNull(name, "name == null");
      this.friendsConnection = Utils.checkNotNull(friendsConnection, "friendsConnection == null");
      this.profileLink = Utils.checkNotNull(profileLink, "profileLink == null");
    }

    public @NotNull String __typename() {
      return this.__typename;
    }

    /**
     * The name of the character
     */
    public @NotNull String name() {
      return this.name;
    }

    /**
     * The friends of the character exposed as a connection with edges
     */
    public @NotNull FriendsConnection friendsConnection() {
      return this.friendsConnection;
    }

    /**
     * Profile link
     */
    public @NotNull Object profileLink() {
      return this.profileLink;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], name);
          writer.writeObject($responseFields[2], friendsConnection.marshaller());
          writer.writeCustom((ResponseField.CustomTypeField) $responseFields[3], profileLink);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Hero{"
          + "__typename=" + __typename + ", "
          + "name=" + name + ", "
          + "friendsConnection=" + friendsConnection + ", "
          + "profileLink=" + profileLink
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Hero) {
        Hero that = (Hero) o;
        return this.__typename.equals(that.__typename)
         && this.name.equals(that.name)
         && this.friendsConnection.equals(that.friendsConnection)
         && this.profileLink.equals(that.profileLink);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= name.hashCode();
        h *= 1000003;
        h ^= friendsConnection.hashCode();
        h *= 1000003;
        h ^= profileLink.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Hero> {
      final FriendsConnection.Mapper friendsConnectionFieldMapper = new FriendsConnection.Mapper();

      @Override
      public Hero map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String name = reader.readString($responseFields[1]);
        final FriendsConnection friendsConnection = reader.readObject($responseFields[2], new ResponseReader.ObjectReader<FriendsConnection>() {
          @Override
          public FriendsConnection read(ResponseReader reader) {
            return friendsConnectionFieldMapper.map(reader);
          }
        });
        final Object profileLink = reader.readCustomType((ResponseField.CustomTypeField) $responseFields[3]);
        return new Hero(__typename, name, friendsConnection, profileLink);
      }
    }
  }

  public static class FriendsConnection {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forList("edges", "edges", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final Optional<List<Edge>> edges;

    private transient volatile String $toString;

    private transient volatile int $hashCode;

    private transient volatile boolean $hashCodeMemoized;

    public FriendsConnection(@NotNull String __typename, @Nullable List<Edge> edges) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.edges = Optional.fromNullable(edges);
    }

    public @NotNull String __typename() {
      return this.__typename;
    }

    /**
     * The edges for each of the character's friends.
     */
    public Optional<List<Edge>> edges() {
      return this.edges;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeList($responseFields[1], edges.isPresent() ? edges.get() : null, new ResponseWriter.ListWriter() {
            @Override
            public void write(List items, ResponseWriter.ListItemWriter listItemWriter) {
              for (Object item : items) {
                listItemWriter.writeObject(((Edge) item).marshaller());
              }
            }
          });
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "FriendsConnection{"
          + "__typename=" + __typename + ", "
          + "edges=" + edges
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof FriendsConnection) {
        FriendsConnection that = (FriendsConnection) o;
        return this.__typename.equals(that.__typename)
         && this.edges.equals(that.edges);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= edges.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<FriendsConnection> {
      final Edge.Mapper edgeFieldMapper = new Edge.Mapper();

      @Override
      public FriendsConnection map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final List<Edge> edges = reader.readList($responseFields[1], new ResponseReader.ListReader<Edge>() {
          @Override
          public Edge read(ResponseReader.ListItemReader listItemReader) {
            return listItemReader.readObject(new ResponseReader.ObjectReader<Edge>() {
              @Override
              public Edge read(ResponseReader reader) {
                return edgeFieldMapper.map(reader);
              }
            });
          }
        });
        return new FriendsConnection(__typename, edges);
      }
    }
  }

  public static class Edge {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forObject("node", "node", null, true, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final Optional<Node> node;

    private transient volatile String $toString;

    private transient volatile int $hashCode;

    private transient volatile boolean $hashCodeMemoized;

    public Edge(@NotNull String __typename, @Nullable Node node) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.node = Optional.fromNullable(node);
    }

    public @NotNull String __typename() {
      return this.__typename;
    }

    /**
     * The character represented by this friendship edge
     */
    public Optional<Node> node() {
      return this.node;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeObject($responseFields[1], node.isPresent() ? node.get().marshaller() : null);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Edge{"
          + "__typename=" + __typename + ", "
          + "node=" + node
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Edge) {
        Edge that = (Edge) o;
        return this.__typename.equals(that.__typename)
         && this.node.equals(that.node);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= node.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Edge> {
      final Node.Mapper nodeFieldMapper = new Node.Mapper();

      @Override
      public Edge map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final Node node = reader.readObject($responseFields[1], new ResponseReader.ObjectReader<Node>() {
          @Override
          public Node read(ResponseReader reader) {
            return nodeFieldMapper.map(reader);
          }
        });
        return new Edge(__typename, node);
      }
    }
  }

  public static class Node {
    static final ResponseField[] $responseFields = {
      ResponseField.forString("__typename", "__typename", null, false, Collections.<ResponseField.Condition>emptyList()),
      ResponseField.forString("name", "name", null, false, Collections.<ResponseField.Condition>emptyList())
    };

    final @NotNull String __typename;

    final @NotNull String name;

    private transient volatile String $toString;

    private transient volatile int $hashCode;

    private transient volatile boolean $hashCodeMemoized;

    public Node(@NotNull String __typename, @NotNull String name) {
      this.__typename = Utils.checkNotNull(__typename, "__typename == null");
      this.name = Utils.checkNotNull(name, "name == null");
    }

    public @NotNull String __typename() {
      return this.__typename;
    }

    /**
     * The name of the character
     */
    public @NotNull String name() {
      return this.name;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ResponseFieldMarshaller marshaller() {
      return new ResponseFieldMarshaller() {
        @Override
        public void marshal(ResponseWriter writer) {
          writer.writeString($responseFields[0], __typename);
          writer.writeString($responseFields[1], name);
        }
      };
    }

    @Override
    public String toString() {
      if ($toString == null) {
        $toString = "Node{"
          + "__typename=" + __typename + ", "
          + "name=" + name
          + "}";
      }
      return $toString;
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (o instanceof Node) {
        Node that = (Node) o;
        return this.__typename.equals(that.__typename)
         && this.name.equals(that.name);
      }
      return false;
    }

    @Override
    public int hashCode() {
      if (!$hashCodeMemoized) {
        int h = 1;
        h *= 1000003;
        h ^= __typename.hashCode();
        h *= 1000003;
        h ^= name.hashCode();
        $hashCode = h;
        $hashCodeMemoized = true;
      }
      return $hashCode;
    }

    public static final class Mapper implements ResponseFieldMapper<Node> {
      @Override
      public Node map(ResponseReader reader) {
        final String __typename = reader.readString($responseFields[0]);
        final String name = reader.readString($responseFields[1]);
        return new Node(__typename, name);
      }
    }
  }
}
